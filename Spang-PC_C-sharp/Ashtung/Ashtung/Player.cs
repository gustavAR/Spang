using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Android;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;

namespace Ashtung
{
    class Player
    {
        public IPhone Phone
        {
            get;
            private set;
        }

        public bool IsAlive
        {
            get;
            set;
        }

        public PlayerInfo Info
        {
            get;
            private set;
        }

        public Worm Worm
        {
            get;
            private set;
        }
                
        public Player(PlayerInfo info)
        {
            this.Info = info;
            this.Worm = new Worm();
            this.IsAlive = true;
            Phone = new AndroidPhone();

        }

        public void Update(GameTime time, Random random)
        {
           // Vector3 orientation = VectorConvert(phone.Orientation);
            //Do stuff with orientation.

            this.Worm.Update(time, random);

            KeyboardState state = Keyboard.GetState();
            if (state.IsKeyDown(Keys.Left))
            {
                this.Worm.Turn(-11);
            }

            if (state.IsKeyDown(Keys.Right))
            {
                this.Worm.Turn(11);
            }

            this.Worm.Move();
        }

        private Vector3 VectorConvert(Spang.Core.Utils.Vector3 vector3)
        {
            return new Vector3(vector3.X, vector3.Y, vector3.Z);
        }

        public void SetRandomStartPos(Random random, Rectangle worldBounds)
        {
            float x = (float)(worldBounds.X + random.NextDouble() * worldBounds.Width);
            float y = (float)(worldBounds.Y + random.NextDouble() * worldBounds.Height);
            float angle = (float)(MathHelper.TwoPi * random.NextDouble());

            this.Worm.Speed = new Vector2((float)Math.Cos(angle), (float)Math.Sin(angle));
            this.Worm.Position = new Vector2(x, y);      
        }
    }
}
