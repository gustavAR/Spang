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

namespace Spang.Core.Utils
{
    public struct Vector3
    {
        public float X, Y, Z;

        public Vector3(float x, float y, float z)
        {
            // TODO: Complete member initialization
            this.X = x;
            this.Y = y;
            this.Z = z;
        }

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
