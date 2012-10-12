using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Ashtung
{
    class LobbyScreen : GameScreen
    {
        TimeSpan timeSpan = TimeSpan.FromSeconds(10);
        Dictionary<Player, bool> isReady;

        public LobbyScreen(Achtung achtung) : base(achtung) 
        {
            this.isReady = new Dictionary<Player, bool>();
            this.achtung.players.ForEach((x) => isReady.Add(x, false));
        }


        public override void Enter()
        {
            PlayerInfo info = new PlayerInfo();
            info.Name = "Player1";
            info.Color = Color.Gold;
            info.ConnectionID = 1;

            Player player = new Player(info);
            this.achtung.players.Add(player);
            this.isReady.Add(player, false);
        }

        public override void ConnectionRecived(Spang.Core.Network.IServer server, Spang.Core.Network.ConnectionEventArgs eventArgs)
        {
            Random random = this.achtung.random;

            PlayerInfo info = new PlayerInfo();
            info.Color = new Color((float)random.NextDouble(), (float)random.NextDouble(), (float)random.NextDouble(), 1.0f);
            info.Name = "Player" + eventArgs.ID;
            info.Connected = true;
            info.ConnectionID = eventArgs.ID;

            Player player = new Player(info);
            player.SetRandomStartPos(random, this.achtung.GraphicsDevice.Viewport.Bounds);

            this.achtung.players.Add(player);
            this.isReady.Add(player, false);
        }

        public override void ConnectionDC(Spang.Core.Network.IServer server, Spang.Core.Network.DisconnectionEventArgs eventArgs)
        {
            Player player = this.achtung.players.Find((x) => x.Info.ConnectionID == eventArgs.ID);
            this.achtung.players.Remove(player);
        }

        public override void MessageRecived(Spang.Core.Network.IServer server, Spang.Core.Network.RecivedEventArgs eventArgs)
        {
            Player player = this.achtung.players.Find((x) => x.Info.ConnectionID == eventArgs.ID);
            if (player != null)
            {
                if (eventArgs.Message is string)
                {
                    string message = eventArgs.Message.ToString();
                    if (message == "") return;
                    player.Info.Name = message;
                    

                    this.isReady[player] = true;
                    if (AllReady())
                    {
                        this.achtung.ChangeScreen(new GamePlayScreen(this.achtung));
                    }
                }
            }
        }

        private bool AllReady()
        {

            foreach (var item in this.isReady.Values)
            {
                if (!item)
                    return true;
            }

            return true;
        }

        public override void Update(Microsoft.Xna.Framework.GameTime time)
        {

        }

        public override void Draw(GameTime time, SpriteBatch spriteBatch)
        {
            spriteBatch.Begin();
            string message = "Connect to be part of the game!";
            Vector2 size = this.achtung.font.MeasureString(message);

            spriteBatch.DrawString(this.achtung.font, message, new Vector2(spriteBatch.GraphicsDevice.Viewport.Width / 2, 50),
                                   Color.Yellow, 0, size * 0.5f, 1.0f,SpriteEffects.None, 0);

            lock (this.achtung._lock)
            {
                foreach (var player in this.achtung.players)
                {
                    string playerStatus = player.Info.Name;
                    if (isReady[player])
                        playerStatus += " Ready!";
                    else
                        playerStatus += " Not Ready!";

                    spriteBatch.DrawString(this.achtung.font, playerStatus, new Vector2(20, 100 + 50 * player.Info.ConnectionID), player.Info.Color); 
                }
            }

            spriteBatch.End();
        }
    }
}
