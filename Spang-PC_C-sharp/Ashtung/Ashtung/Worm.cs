using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;

namespace Ashtung
{
    class Worm
    {
        private const int WormSize = 5;
        private const int TURN_TRESHOLD = 10;
        private const float TURN_ANGLE = (float)Math.PI / 32;



        public Vector2 Position
        {
            get;
            set;
        }

        public Vector2 Speed
        {
            get;
            set;
        }

        public void Turn(int dx)
        {
            float magnitude = Speed.Length();
            float angle = (float)Math.Atan2(Speed.Y, Speed.X);

            if (dx < TURN_TRESHOLD)
            {
                angle -= TURN_ANGLE;
            }

            if (dx > TURN_TRESHOLD)
            {
                angle += TURN_ANGLE;
            }

            this.Speed = new Vector2((float)Math.Cos(angle), (float)Math.Sin(angle)) * magnitude;
        }

        public void Move()
        {
            this.Position += Speed;
        }

        public Vector2 Origin
        {
            get
            {
                return new Vector2(WormSize / 2.0f, WormSize / 2);
            }
        }

        public Rectangle Bounds
        {
            get
            {
                return new Rectangle((int)(Position.X - Origin.X),
                                     (int)(Position.Y - Origin.Y),
                                     WormSize,
                                     WormSize);
            }
        }

        public bool Collision(Color[] pixels, int height, int width)
        {
            float angle = (float)Math.Atan2(Speed.Y, Speed.X);

            Vector2 v = this.Position + new Vector2((float)Math.Cos(angle), (float)Math.Sin(angle)) * (this.Origin.Length() + 0.5f);
            int index = (int)(v.X) + (int)(v.Y) * width;

            if (index < 0 || index > pixels.Length || (pixels[index] != Color.Gold && pixels[index] != Color.Transparent))
            {
                Console.WriteLine("Collision Occured!!");
                return true;
            }

            return false;
        }

    }
}
