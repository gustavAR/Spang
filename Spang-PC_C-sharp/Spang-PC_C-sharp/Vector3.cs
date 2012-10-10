using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    struct Vector3
    {
        public float X, Y, Z;

        public static bool operator != (Vector3 a, Vector3 b)
        {
            return a.X != b.X && a.Y != b.Y && a.Z != b.Z;
        }

        public static bool operator == (Vector3 a, Vector3 b)
        {
            return a.X == b.X && a.Y == b.Y && a.Z == b.Z;
        }

        public static Vector3 operator -(Vector3 a, Vector3 b)
        {
            return new Vector3() { X = a.X - b.X, Y = a.Y - b.Y, Z = a.Z - b.Z };
        }

        public static Vector3 operator +(Vector3 a, Vector3 b)
        {
            return new Vector3() { X = a.X + b.X, Y = a.Y + b.Y, Z = a.Z + b.Z };
        }


        public static float Distance(Vector3 a, Vector3 b)
        {
            return (float)Math.Sqrt((a.X - b.X) * (a.X - b.X) +
                             (a.Y - b.Y) * (a.Y - b.Y) +
                             (a.Z - b.Z) * (a.Z - b.Z));
        }
    }
}
