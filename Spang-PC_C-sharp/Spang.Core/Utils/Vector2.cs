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
