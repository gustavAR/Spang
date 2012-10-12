using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Timers;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Ashtung
{
    class GamePlayScreen : GameScreen
    {
        private SpriteFont font;
        volatile int countDown;

        public override void Enter()
        {
            Timer timer = new Timer(TimeSpan.FromSeconds(1).Milliseconds);
            timer.AutoReset = true;
            timer.Elapsed += (s, e) =>
            {
                countDown--;
                if (countDown == 0)
                    timer.Stop();
            };

            timer.Start();
        }

        public override void Exit()
        {
            //DO nothing 
        }

        public override void LoadContent(Microsoft.Xna.Framework.Content.ContentManager manager)
        {
            this.font = manager.Load<SpriteFont>("SpriteFont1");
        }

        public override void ConnectionRecived(Spang.Core.Network.IServer server, Spang.Core.Network.ConnectionEventArgs eventArgs)
        {
            throw new NotImplementedException();
        }

        public override void ConnectionDC(Spang.Core.Network.IServer server, Spang.Core.Network.DisconnectionEventArgs eventArgs)
        {
            throw new NotImplementedException();
        }

        public override void MessageRecived(Spang.Core.Network.IServer server, Spang.Core.Network.RecivedEventArgs eventArgs)
        {
            throw new NotImplementedException();
        }

        public override void Update(Microsoft.Xna.Framework.GameTime time)
        {
            if (countDown == 0)
            {

            }
        }

        public override void Draw(GameTime time, SpriteBatch spriteBatch)
        {
            if (countDown > 0)
            {
                spriteBatch.DrawString(this.font, "Game Staring in " + this.countDown + " seconds!", new Vector2(40, 200), Color.Green);
            }
            else
            {
                
            }
        }
    }
}
