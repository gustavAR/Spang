using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    struct Vector2
    {
        public float X;
        public float Y;

        public Vector2(float x, float y)
        {
            // TODO: Complete member initialization
            this.X = x;
            this.Y = y;
        }


        public static bool operator ==(Vector2 a, Vector2 b)
        {
            return a.X == b.X && a.Y == b.Y;
        }

        public static bool operator !=(Vector2 a, Vector2 b)
        {
            return a.X != b.X && a.Y != b.Y;
        }
    }
}
