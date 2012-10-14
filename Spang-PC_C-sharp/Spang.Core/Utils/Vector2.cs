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
    public struct Vector2
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

        public static Vector2 operator -(Vector2 a, Vector2 b)
        {
            return new Vector2(a.X - b.X, a.Y - b.Y);
        }

        public static Vector2 operator +(Vector2 a, Vector2 b)
        {
            return new Vector2(a.X + b.X, a.Y + b.Y);
        }


        public static float Distance(Vector2 a, Vector2 b)
        {
            return (float)Math.Sqrt((a.X - b.X) * (a.X - b.X) +
                             (a.Y - b.Y) * (a.Y - b.Y));
        }

        public float Length()
        {
            return (float)Math.Sqrt(X * X + Y * Y);
        }

        public static float Dot(Vector2 a, Vector2 b)
        {
            return (float)Math.Acos((a.X * b.X + a.Y * b.Y) / (a.Length() * b.Length()));
        }
    }
}
