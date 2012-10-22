package serialization;

import network.messages.SensorEvent;

import org.junit.Before;
import org.junit.Test;

import utils.Packer;
import utils.UnPacker;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SerializeManagerTests {

	private SerializeManager manager;
	
	@Before
	public void setup() {
		this.manager = new SerializeManager();
	}
	
	 
	@Test
	public void testRegisterSerializer() {
		manager.registerSerilizer(new StringSerializer());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIfExceptionIsThrownOnDuplicateSerializer() {
		manager.registerSerilizer(new StringSerializer());
		manager.registerSerilizer(new StringSerializer());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIFExceptionIFThrownOnDupplicateTypeSerializers() {
		//Mokito refused to take when(serializer.getSerilalizableType()).thenReturn(String.class);
		//as a legal argument so for this reason a new serializer was manually mocked.
		ISerializer serializer = new ISerializer() {
			public void serialize(Packer packer, Object message) {}
			public Class<?> getSerializableType() { return String.class; }
			public Object deserialize(UnPacker unpacker){ return null;}
		};
		
		manager.registerSerilizer(new StringSerializer());
		manager.registerSerilizer(serializer);	
	}
	
	@Test
	public void testIfCanSerialize() {
		manager.registerSerilizer(new StringSerializer());
		
		Packer packer = new Packer();
		manager.serialize(packer, "Hello");
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testIFExceptionIsThrownOnTypeThatCannotBeSerialized() {		
		Packer packer = new Packer();
		manager.serialize(packer, "Hello");		
	}
	
	@Test
	public void testIfCorredIdsAreGeneratedOnSerialization() {
		manager.registerSerilizer(new StringSerializer());
		manager.registerSerilizer(new ByteArraySerializer());
		manager.registerSerilizer(new SensorEventSerializer());
		
		Packer packer = new Packer();
		assertCorrectID(packer, "Hello", (byte)0);
		assertCorrectID(packer, new byte[0], (byte)1);
		assertCorrectID(packer, new SensorEvent(0, new float[0]), (byte)2);
	}
	
	private void assertCorrectID(Packer packer, Object toSerialize, byte expectedID) {
		packer.clear();
		manager.serialize(packer, toSerialize);
		UnPacker unPacker = new UnPacker(packer.getPackedData());
		assertEquals(expectedID, unPacker.unpackByte());	
	}
	
	@Test
	public void testIfCanDeserialize() {
		this.manager.registerSerilizer(new StringSerializer());
		String expected = "Hello";
		Packer packer = new Packer();
		this.manager.serialize(packer, expected);
	
		String result = (String)this.manager.deserialize(new UnPacker(packer.getPackedData()));
		assertEquals(result, expected);
	}	
}