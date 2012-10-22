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

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import utils.Packer;
import utils.UnPacker;

/**
 * Handles the serialization of objects.
 * @author Lukas Kurtyan
 */
@SuppressLint("UseSparseArrays")
public class SerializeManager { 
	private int nextID = 0;
	
	private Map<Integer, Class<?>> idToType;
	private Map<Class<?>, Integer> typeToID;
	private Map<Class<?>, ISerializer> serializerByType;

	public SerializeManager()
	{ 
		this.idToType = new HashMap<Integer, Class<?>>();
		this.typeToID = new HashMap<Class<?>, Integer>();
		this.serializerByType = new HashMap<Class<?>, ISerializer>();
	}

	/**
	 * 
	 * @param serializer
	 */
	public void registerSerializer(ISerializer serializer)
	{
		if(serializerByType.containsKey(serializer.getSerializableType()))
			throw new IllegalArgumentException("The serializable type" + 
					serializer.getSerializableType() + 
					"is already pressent in the serializer");

		int id = nextID++;
		this.idToType.put(id, serializer.getSerializableType());
		this.typeToID.put(serializer.getSerializableType(), id);
		this.serializerByType.put(serializer.getSerializableType(), serializer);
	}

	public void serialize(Packer packer, Object message)
	{
		if (!this.serializerByType.containsKey(message.getClass()))
			throw new IllegalArgumentException("Cannot serialize type: " + message.getClass());

		this.PackID(packer, this.typeToID.get(message.getClass()));

		ISerializer serializer = this.serializerByType.get(message.getClass());
		serializer.serialize(packer, message);
	}

	public Object deserialize(UnPacker unpacker)
	{
		int id = this.UnpackID(unpacker);
		ISerializer serializer = this.serializerByType.get(this.idToType.get(id));
		return serializer.deserialize(unpacker);
	}

	private int UnpackID(UnPacker unpacker)
	{
		if (this.nextID < Byte.MAX_VALUE) {
			return unpacker.unpackByte();
		} else if (this.nextID < Short.MAX_VALUE) {
			return unpacker.unpackShort();
		} else {
			return unpacker.unpackInt();
		}
	}

	private void PackID(Packer packer, int id)
	{
		if (this.nextID < Byte.MAX_VALUE) {
			packer.packByte((byte)id);
		} else if (this.nextID < Short.MAX_VALUE) {
			packer.packShort((short)id);
		} else	{
			packer.packInt(id);
		}
	}
}
