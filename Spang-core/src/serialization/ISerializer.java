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

import utils.Packer;
import utils.UnPacker;

/**
 * Implementations of this interface are able to serialize and deserialize a 
 * specific type of java object.
 * 
 * @author Lukas Kurtyan
 *
 */
public interface ISerializer {
	
	/**
	 * Serializes the object using the packer.
	 * @param packer the packer used. 
	 * @param message the object to serialize.
	 * @throws ClassCastException if the toSerialize.getClass() does not equal getSerializableTpe()
	 */
	public void serialize(Packer packer, Object toSerialize);
	
	/**
	 * Deserializes a object from data in an unpacker to a java Object.
	 * @param unpacker the unpacker with the data.
	 * @throws OverflowException if the data in the unpacker is invalid.
	 * @return a deserialized object.
	 */
	public Object deserialize(UnPacker unpacker);
	
	/**
	 * The type this class is capable of serializing.
	 * @return a class.
	 */
	public Class<?> getSerializableType();
}
