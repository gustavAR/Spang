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
        public abstract void Enter();
        public abstract void Exit();
        public abstract void LoadContent(ContentManager manager);

        public abstract void ConnectionRecived(IServer server, ConnectionEventArgs eventArgs);
        public abstract void ConnectionDC(IServer server, DisconnectionEventArgs eventArgs);
        public abstract void MessageRecived(IServer server, RecivedEventArgs eventArgs);

        public abstract void Update(GameTime time);
        public abstract void Draw(GameTime time, SpriteBatch spriteBatch);
    }
}
