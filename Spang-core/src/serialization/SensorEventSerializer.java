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

import network.messages.SensorEvent;
import serialization.Serializer;
import utils.Packer;
import utils.UnPacker;

/**
 * A class that can serialize SensorEvents.
 * @author Lukas Kurtyan
 */
public class SensorEventSerializer extends Serializer<SensorEvent>{

	public SensorEventSerializer() {
		super(SensorEvent.class);
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
    public void serializeInternal(Packer packer, SensorEvent message)
    {
        packer.packByte((byte)message.SensorID);
        packer.packShort((byte)message.SensorData.length);
        packer.packFloatArray(message.SensorData);
    }

	/**
	 * {@inheritDoc}
	 */
    public SensorEvent deserialize(UnPacker unpacker)
    {
        int id = unpacker.unpackByte();
        int length = unpacker.unpackByte();
        float[] data = unpacker.unpackFloatArray(length);

        return new SensorEvent(id, data);
    }
}
