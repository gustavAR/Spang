using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Spang.Core.Utils
{
    class Packer
    {
        private const int DEFAULT_CAPACITY = 4;

        MemoryStream data;
        BinaryWriter writer;

        public Packer() : this(DEFAULT_CAPACITY) { }

        public Packer(int capacity)
        {
            data = new MemoryStream(capacity);
            writer = new BinaryWriter(data);
        }

        public byte[] GetPackedData() 
        {
            byte[] copy = new byte[data.Position];
            this.data.Position = 0;
            this.data.Read(copy, 0, copy.Length);
            return copy;
        }
        
        public void clear() 
        {
            this.data.Position = 0;
        }

        public Packer Pack(byte value) 
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(byte[] array) 
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }
            return this; 
        }
        
        public Packer Pack(sbyte value) { return this; }
        public Packer Pack(sbyte[] array)     
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this; 
        }
        
        public Packer Pack(short value) { return this; }
        public Packer Pack(short[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }

        public Packer Pack(ushort value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(ushort[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }

        public Packer Pack(int value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(int[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }
        
        public Packer Pack(uint value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(uint[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }
        
        public Packer Pack(long value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(long[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }

        public Packer Pack(ulong value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(ulong[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }

        public Packer Pack(float value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(float[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }

        public Packer Pack(double value)
        {
            this.writer.Write(value);
            return this;
        }

        public Packer Pack(double[] array)
        {
            foreach (var item in array)
            {
                this.writer.Write(item);
            }

            return this;
        }

        public Packer Pack(string str)
        {
            byte[] encoded = Encoding.UTF8.GetBytes(str);
            this.writer.Write(encoded.Length);
            this.Pack(encoded);
            return this;
        }

        public Packer PackHalfFloat(float value)
        {
            this.writer.Write(fromFloat(value));   
            return this;
        }

        public Packer PackHalfFloats(float[] array)
        {
            foreach (var item in array)
            {
                this.PackHalfFloat(item);
            }
            return this;
        }

        //Modified Public Domain.
        private static ushort fromFloat(float fval)
        {
            int fbits = SingleToInt32Bits(fval);
	    int sign = fbits >> 16 & 0x8000;          // sign only
	    int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

	    if( val >= 0x47800000 )               // might be or become NaN/Inf
	    {                                     // avoid Inf due to rounding
	        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
	        {                                 // is or must become NaN/Inf
	            if( val < 0x7f800000 )        // was value but too large
	                return (ushort) (sign | 0x7c00);     // make it +/-Inf
                return (ushort)(sign | 0x7c00 |        // remains +/-Inf or NaN
	                ( fbits & 0x007fffff ) >> 13); // keep NaN (and Inf) bits
	        }
            return (ushort)(sign | 0x7bff);             // unrounded not quite Inf
	    }
	    if( val >= 0x38800000 )               // remains normalized value
            return (ushort)(sign | val - 0x38000000 >> 13); // exp - 127 + 15
	    if( val < 0x33000000 )                // too small for subnormal
            return (ushort)sign;                      // becomes +/-0
	    val = ( fbits & 0x7fffffff ) >> 23;  // tmp exp for subnormal calc
        return (ushort)(sign | ((fbits & 0x7fffff | 0x800000) // add subnormal bit
	         + ( 0x800000 >> val - 102 )     // round depending on cut off
	      >> 126 - val ));   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
        }

        private static int SingleToInt32Bits(float value)
        {
            return BitConverter.ToInt32(BitConverter.GetBytes(value), 0);
        }

    }
}
