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
 * A class that can serialize byte arrays.
 * @author Lukas Kurtyan
 */
public class ByteArraySerializer extends Serializer<byte[]> {
	
	public ByteArraySerializer() {
		super(byte[].class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void serializeInternal(Packer packer, byte[] message) {
		packer.packInt(message.length);
		packer.packByteArray(message);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] deserialize(UnPacker unpacker) {
		int size = unpacker.unpackInt();
		return unpacker.unpackByteArray(size);
	}
}
