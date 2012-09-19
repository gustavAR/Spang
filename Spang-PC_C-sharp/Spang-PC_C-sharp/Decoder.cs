using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class Decoder
    {

        public Accelerometer decodeAccelerometer(byte[] data)
        {
            float[] floatData = new float[3];
            Buffer.BlockCopy(data, 1, floatData, 0, 12);
            
            return new Accelerometer
            {
                X = floatData[0],
                Y = floatData[1],
                Z = floatData[2]
            };
	    }
    }
}
