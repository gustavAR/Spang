using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using Spang.Core.Android;

namespace Ashtung
{
    class GamePlayScreen : GameScreen
    {
        enum GameState { Running, Paused }

        private GameState state = GameState.Running;


        public GamePlayScreen(Achtung achtung) : base(achtung) { }


        public override void Exit()
        {
            this.achtung.GraphicsDevice.SetRenderTarget(achtung.renderTarget);
            this.achtung.GraphicsDevice.Clear(Color.Black);
            this.achtung.GraphicsDevice.SetRenderTarget(null);
        }

        public override void ConnectionRecived(Spang.Core.Network.IServer server, Spang.Core.Network.ConnectionEventArgs eventArgs)
        {
            if (state == GameState.Paused)
            {
                Player player = this.achtung.players.Find((x) => x.Info.ConnectionID == eventArgs.ID);
                if(player != null)
                    player.Info.Connected = true;

                if (AllPlayersConnected())
                {
                    state = GameState.Running;
                }
            }
        }

        private bool AllPlayersConnected()
        {
            return this.achtung.players.Find((x) => !x.Info.Connected) == null;
        }

        public override void ConnectionDC(Spang.Core.Network.IServer server, Spang.Core.Network.DisconnectionEventArgs eventArgs)
        {
            Player player = this.achtung.players.Find((x) => x.Info.ConnectionID == eventArgs.ID);
            if (player != null)
            {
                player.Info.Connected = false;
                state = GameState.Paused;
            }
        }

        public override void MessageRecived(Spang.Core.Network.IServer server, Spang.Core.Network.RecivedEventArgs eventArgs)
        {
            Player player = this.achtung.players.Find((x) => x.Info.ConnectionID == eventArgs.ID);
            if (player != null)
            {
                if (eventArgs.Message is IPhoneMessage)
                {
                    player.Phone.ProcessMessage((IPhoneMessage)eventArgs.Message);
                }
                else if (eventArgs.Message is SensorEvent)
                {
                    player.Phone.ProcessMessage((SensorEvent)eventArgs.Message);
                    Console.WriteLine("Kind of got some SensorEvent");
                }

            }
        }

        public override void Update(Microsoft.Xna.Framework.GameTime time)
        {
            if (state == GameState.Running)
            {
                Color[] collisionData = new Color[achtung.renderTarget.Width * achtung.renderTarget.Height];
                achtung.renderTarget.GetData<Color>(collisionData);

                foreach (var player in achtung.players)
                {
                    if (player.IsAlive)
                    {
                        player.Update(time, achtung.random);
                        if (WormCollision(player, collisionData))
                        {
                            player.IsAlive = false;
                            if (PlayersAlive() == 1)
                            {
                                this.achtung.ChangeScreen(new WinningScreen(this.achtung));
                                return;
                            }
                        }
                    }
                }
            }
            else
            {
                //Do something else.
            }
        }

        private int PlayersAlive()
        {
            int c = 0;
            foreach (var player in achtung.players)
            {
                if (player.IsAlive)
                    c++;
            }
            return c;
        }

        private bool WormCollision(Player player, Color[] data)
        {
            return player.Worm.Collision(data, achtung.renderTarget.Height, achtung.renderTarget.Width);
        }

        public override void Draw(Microsoft.Xna.Framework.GameTime time, Microsoft.Xna.Framework.Graphics.SpriteBatch spriteBatch)
        {
            RenderTarget2D renderTarget = achtung.renderTarget;

            achtung.GraphicsDevice.SetRenderTarget(renderTarget);
            spriteBatch.Begin();
            foreach (var player in achtung.players)
            {
                player.Worm.Draw(spriteBatch, player.Info.Color, achtung.pixel);
            }

            spriteBatch.End();

            spriteBatch.GraphicsDevice.SetRenderTarget(null);


            spriteBatch.Begin();
            spriteBatch.Draw(renderTarget, renderTarget.GraphicsDevice.Viewport.Bounds, Color.White);
            foreach (var player in this.achtung.players)
            {
                player.Worm.Draw(spriteBatch, player.Info.Color, achtung.pixel, true);
            }
            spriteBatch.End();

            spriteBatch.Begin();
            lock (achtung._lock)
            {
                for (int i = 0; i < achtung.players.Count; i++)
                {
                    Vector2 offset = new Vector2(20, i * 50);
                    if (achtung.players[i].Info.Name != null)
                        spriteBatch.DrawString(achtung.font, achtung.players[i].Info.Name, offset, achtung.players[i].Info.Color);
                }
            }
            spriteBatch.End();
        }
    }
}
