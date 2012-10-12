using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Ashtung
{
    class WinningScreen : GameScreen
    {
        TimeSpan timeSpan = TimeSpan.FromSeconds(5);
        string winner;

        public WinningScreen(Achtung achtung) : base(achtung) { }

        public override void Enter()
        {
            this.achtung.server.Stop();
            winner = this.achtung.players.Find((x) => x.IsAlive).Info.Name;
        }

        public override void ConnectionRecived(Spang.Core.Network.IServer server, Spang.Core.Network.ConnectionEventArgs eventArgs)
        {
        }

        public override void ConnectionDC(Spang.Core.Network.IServer server, Spang.Core.Network.DisconnectionEventArgs eventArgs)
        {
        }

        public override void MessageRecived(Spang.Core.Network.IServer server, Spang.Core.Network.RecivedEventArgs eventArgs)
        {
        }

        public override void Update(Microsoft.Xna.Framework.GameTime time)
        {
            timeSpan -= time.ElapsedGameTime;
            if (timeSpan < TimeSpan.Zero)
            {
                this.achtung.ChangeScreen(new LobbyScreen(this.achtung));
            }
        }

        public override void Draw(Microsoft.Xna.Framework.GameTime time, Microsoft.Xna.Framework.Graphics.SpriteBatch spriteBatch)
        {
            string message = "Winner is " + winner;
            spriteBatch.Begin();
            Vector2 size = this.achtung.font.MeasureString(message);

            spriteBatch.DrawString(this.achtung.font, message, new Vector2(spriteBatch.GraphicsDevice.Viewport.Width / 2, 50),
                                   Color.Yellow, 0, size * 0.5f, 1.0f, SpriteEffects.None, 0);
            spriteBatch.End();
        }
    }
}
