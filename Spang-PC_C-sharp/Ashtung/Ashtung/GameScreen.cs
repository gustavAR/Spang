using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Spang.Core.Network;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Graphics;

namespace Ashtung
{
    abstract class GameScreen
    {
        protected Achtung achtung;

        public GameScreen(Achtung achtung)
        {
            this.achtung = achtung;
        }

        public virtual void Enter() { }
        public virtual void Exit() { }
        public virtual void LoadContent(ContentManager manager) { }

        public abstract void ConnectionRecived(IServer server, ConnectionEventArgs eventArgs);
        public abstract void ConnectionDC(IServer server, DisconnectionEventArgs eventArgs);
        public abstract void MessageRecived(IServer server, RecivedEventArgs eventArgs);

        public abstract void Update(GameTime time);
        public abstract void Draw(GameTime time, SpriteBatch spriteBatch);
    }
}
