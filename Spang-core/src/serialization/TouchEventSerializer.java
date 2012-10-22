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

import network.messages.Touch;
import network.messages.TouchEvent;
import serialization.Serializer;
import utils.Packer;
import utils.UnPacker;

public class TouchEventSerializer extends Serializer<TouchEvent>{

	public TouchEventSerializer() {
		super(TouchEvent.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void serializeInternal(Packer packer, TouchEvent message) {
		packer.packByte((byte)message.data.length);
		for (Touch touch : message.data) {
			packer.packShort((short)touch.X);
			packer.packShort((short)touch.Y);
			packer.packByte((byte)(touch.Pressure * 256.0f));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TouchEvent deserialize(UnPacker unpacker) {
		int count = unpacker.unpackByte();
		Touch[] touches = new Touch[count];
		for (int i = 0; i < count; i++) {
			float x = unpacker.unpackShort();
			float y = unpacker.unpackShort();
			float pressure = (unpacker.unpackByte() / 256.0f);
			touches[i] = new Touch(x,y,pressure);
		}
		
		return new TouchEvent(touches);				
	}

}
