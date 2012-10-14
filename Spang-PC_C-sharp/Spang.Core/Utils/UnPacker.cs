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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Spang.Core.Utils
{
    public class UnPacker
    {
        MemoryStream data;
        BinaryReader reader;

        public UnPacker(byte[] recivedMessage)
        {
            this.data = new MemoryStream(recivedMessage);
            this.reader = new BinaryReader(this.data);
        }

        public int remaining()
        {
            return (int)(this.data.Length - this.data.Position);
        }

        public byte UnpackByte()
        {
            return this.reader.ReadByte();
        }

        public byte[] UnpackByteArray(int p)
        {
            return this.reader.ReadBytes(p);
        }

        public sbyte UnpackSByte()
        {
            return this.reader.ReadSByte();
        }

        public sbyte[] UnpackSByteArray(int p)
        {
            sbyte[] array = new sbyte[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadSByte();
            }
            return array;
        }

        public short UnpackShort()
        {
            return this.reader.ReadInt16();
        }

        public short[] UnpackShortArray(int p)
        {
            short[] array = new short[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadInt16();
            }
            return array;
        }

        public ushort UnpackUShort()
        {
            return this.reader.ReadUInt16();
        }

        public ushort[] UnpackUShortArray(int p)
        {
            ushort[] array = new ushort[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadUInt16();
            }
            return array;
        }

        public int UnpackInteger()
        {
            return this.reader.ReadInt32();
        }

        public int[] UnpackIntegerArray(int p)
        {
            int[] array = new int[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadInt32();
            }
            return array;
        }

        public uint UnpackUint()
        {
            return this.reader.ReadUInt32();
        }

        public uint[] UnpackUintArray(int p)
        {
            uint[] array = new uint[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadUInt32();
            }
            return array;
        }


        public long UnpackLong()
        {
            return this.reader.ReadInt64();
        }

        public long[] UnpackLongArray(int p)
        {
            long[] array = new long[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadInt64();
            }
            return array;
        }

        public ulong UnpackULong()
        {
            return this.reader.ReadUInt64();
        }

        public ulong[] UnpackULongArray(int p)
        {
            ulong[] array = new ulong[p];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadUInt64();
            }
            return array;
        }

        public float UnpackFloat()
        {
            return this.reader.ReadSingle();
        }

        public float[] UnpackFloatArray(int length)
        {
            float[] array = new float[length];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadSingle();
            }
            return array;
        }

        public double UnpackDouble()
        {
            return this.reader.ReadDouble();
        }

        public double[] UnpackDoubleArray(int length)
        {
            double[] array = new double[length];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.reader.ReadDouble();
            }
            return array;
        }

        public string UnpackString()
        {
            int length = this.reader.ReadInt32();
            byte[] strdata = this.reader.ReadBytes(length);
            return Encoding.UTF8.GetString(strdata);
        }

        public float UnpackHalfFloat()
        {
            ushort value = this.reader.ReadUInt16();
            return toFloat((int)(value));
        }

        public float[] PackHalfFloats(int length)
        {
            float[] array = new float[length];
            for (int i = 0; i < array.Length; i++)
            {
                array[i] = this.UnpackHalfFloat();
            }
            return array;
        }

        private static float intBitsToFloat(int value)
        {
            return BitConverter.ToSingle(BitConverter.GetBytes(value), 0);
        }

        // Public Domain http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
        private static float toFloat(int hbits)
        {
            int mant = hbits & 0x03ff;            // 10 bits mantissa
            int exp = hbits & 0x7c00;            // 5 bits exponent
            if (exp == 0x7c00)                   // NaN/Inf
                exp = 0x3fc00;                    // -> NaN/Inf
            else if (exp != 0)                   // normalized value
            {
                exp += 0x1c000;                   // exp - 15 + 127
                if (mant == 0 && exp > 0x1c400)  // smooth transition
                    return intBitsToFloat((hbits & 0x8000) << 16
                                                    | exp << 13 | 0x3ff);
            }
            else if (mant != 0)                  // && exp==0 -> subnormal
            {
                exp = 0x1c400;                    // make it normal
                do
                {
                    mant <<= 1;                   // mantissa * 2
                    exp -= 0x400;                 // decrease exp by 1
                } while ((mant & 0x400) == 0); // while not normal
                mant &= 0x3ff;                    // discard subnormal bit
            }                                     // else +/-0 -> +/-0
            return intBitsToFloat(          // combine all parts
                (hbits & 0x8000) << 16          // sign  << ( 31 - 15 )
                | (exp | mant) << 13);         // value << ( 23 - 10 )
        }
    }
}