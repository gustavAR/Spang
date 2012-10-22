/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
package serialization;

import static org.junit.Assert.assertEquals;
import network.messages.SensorEvent;

import org.junit.Before;
import org.junit.Test;

import utils.Packer;
import utils.UnPacker;

public class SerializeManagerTests {

	private SerializeManager manager;
	
	@Before
	public void setup() {
		this.manager = new SerializeManager();
	}
	
	 
	@Test
	public void testRegisterSerializer() {
		manager.registerSerializer(new StringSerializer());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIfExceptionIsThrownOnDuplicateSerializer() {
		manager.registerSerializer(new StringSerializer());
		manager.registerSerializer(new StringSerializer());
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
		
		manager.registerSerializer(new StringSerializer());
		manager.registerSerializer(serializer);	
	}
	
	@Test
	public void testIfCanSerialize() {
		manager.registerSerializer(new StringSerializer());
		
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
		manager.registerSerializer(new StringSerializer());
		manager.registerSerializer(new ByteArraySerializer());
		manager.registerSerializer(new SensorEventSerializer());
		
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
		this.manager.registerSerializer(new StringSerializer());
		String expected = "Hello";
		Packer packer = new Packer();
		this.manager.serialize(packer, expected);
	
		String result = (String)this.manager.deserialize(new UnPacker(packer.getPackedData()));
		assertEquals(result, expected);
	}	
}