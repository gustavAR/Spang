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
    }
}
