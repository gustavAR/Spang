package serialization;

import java.util.Arrays;
import java.util.Collection;

import network.messages.SensorEvent;
import network.messages.Touch;
import network.messages.TouchEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import utils.Packer;
import utils.UnPacker;
import static junit.framework.Assert.*;

@RunWith(value = Parameterized.class)
public class SerializerTests {

	private final Class<?> clazz;
	private final ISerializer serializer;
	private final Object toSerialize;

	public SerializerTests(Class<?> clazz, ISerializer serializer, Object toSerialize) {
		this.clazz = clazz;
		this.serializer = serializer;
		this.toSerialize = toSerialize;
	}

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] { { String.class, new StringSerializer(), "Hello" }, 
										   { TouchEvent.class, new TouchEventSerializer(), 
										   	 new TouchEvent(new Touch[] { new Touch(10,412,0.4f), new Touch(14,42,0.4f)} )}, 
										   { SensorEvent.class, new SensorEventSerializer(), 
										     new SensorEvent(8, new float[] {-34f, 21f, 0.3f }) },
										   };
		return Arrays.asList(data);
	}	
	
	
	@Test
	public void testIfClassAbleToSerializeIsCorrect() {
		assertEquals(clazz, serializer.getSerializableType());
	}
	
	
	@Test
	public void testIfCanSerialize() {
		//Fails if exception is thrown. 
		Packer packer = new Packer();
		serializer.serialize(packer, toSerialize);
	}
	
	@Test
	public void testIfCanDeserialize() {
		Packer packer = new Packer();
		serializer.serialize(packer, toSerialize);
		
		UnPacker unpacker = new UnPacker(packer.getPackedData());
		Object deserialized = serializer.deserialize(unpacker);
		
		assertEquals(deserialized, toSerialize);
	}
}