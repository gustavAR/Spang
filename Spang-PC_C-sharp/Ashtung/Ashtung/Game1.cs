using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;
using Spang.Core.Network;
using Spang.Core.Serialization;

using System.Net;
using System.Net.Sockets;
using System.Drawing.Imaging;
using Spang.Core.Android;

namespace Ashtung
{
    enum GameState
    {
        Lobby,
        Playing,
        Paused,
        GameOver
    }


    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class Game1 : Microsoft.Xna.Framework.Game
    {
        private object _lock = new object();
        private const int PORT = 23452;
        private GameState state = GameState.Lobby;

        private List<PlayerInfo> players = new List<PlayerInfo>();
        private SpriteFont font;

        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        IServer server;

        private Worm worm;


        Texture2D pixel;

        private RenderTarget2D renderTarget;


        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            SerializeManager manager = new SerializeManager();
            manager.RegisterSerilizer(new TouchEventSerializer());
            manager.RegisterSerilizer(new SensorEventSerializer());
            manager.RegisterSerilizer(new StringSerializer());

            server = new Server(manager);
            server.Connected += (s,e) => 
            {
                lock (_lock) this.ConnectionRecived(s, e);
            };
            server.Recived += (s, e) =>
            {
                lock (_lock) this.MesssageRecived(s, e);
            };
            server.Dissconnected += (s, e) =>
            {
                lock (_lock) this.ConnectionDC(s, e);
            }; 

            #region Generate and show QR

            //Find our local IP-address
            IPHostEntry host;
            string localIP = "";
            host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (IPAddress ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork)
                {
                    localIP = ip.ToString();
                }
            }

            QRCodeGenerator.ShowQRCode(localIP + "/" + PORT, 120, 120, ImageFormat.Png);//120 are ok dimensions

            #endregion

            base.Initialize();
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);

            // TODO: use this.Content to load your game content here
            server.Start(PORT);

            this.pixel = Content.Load<Texture2D>("Circlepng");


            this.renderTarget = new RenderTarget2D(this.GraphicsDevice, this.GraphicsDevice.Viewport.Width, 
                                                   this.GraphicsDevice.Viewport.Height, false, SurfaceFormat.Color, 
                                                   DepthFormat.None, 1, RenderTargetUsage.PreserveContents);

            this.GraphicsDevice.SetRenderTarget(renderTarget);
            this.GraphicsDevice.Clear(Color.Gold);
            this.GraphicsDevice.SetRenderTarget(null);


            this.font = Content.Load<SpriteFont>("SpriteFont1");

            this.worm = new Worm();
            this.worm.Position = new Vector2(200, 200);
            this.worm.Speed = new Vector2(0.5f,0);
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            server.Stop();
        }

        void ConnectionDC(IServer sender, DisconnectionEventArgs eventArgs)
        {
            if (state == GameState.Lobby)
            {
                this.players.RemoveAt(eventArgs.ID);
            }
            else
            {
                PlayerInfo info = new PlayerInfo();
                info.Color = Color.Gray;
            }

        }

        void MesssageRecived(IServer sender, RecivedEventArgs eventArgs)
        {
            if (state == GameState.Lobby)
            {

                if (eventArgs.Message is String)
                {
                    String name = (String)eventArgs.Message;
                    PlayerInfo info = this.players[eventArgs.ID];
                    info.Name = name;
                }
            }
        }

        void ConnectionRecived(IServer sender, ConnectionEventArgs eventArgs)
        {
            if (state == GameState.Lobby)
            {
                PlayerInfo info = new PlayerInfo();
                info.Color = Color.Gold;
                info.Name = "Player" + this.players.Count;
                info.Connected = true;
                info.ConnectionID = eventArgs.ID;

                this.players.Insert(eventArgs.ID, info);
            }
            else
            {
            }
        }


        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            // Allows the game to exit
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed)
                this.Exit();

            KeyboardState state = Keyboard.GetState();
            
            if(state.IsKeyDown(Keys.Left)) 
            {
                this.worm.Turn(-11);
            }

            if(state.IsKeyDown(Keys.Right)) 
            {
                this.worm.Turn(11);
            }

            this.worm.Move();

            this.WormCollision(worm);
            base.Update(gameTime);
        }

        private bool WormCollision(Worm worm)
        {
            Color[] collisionData = new Color[this.renderTarget.Width * this.renderTarget.Height];
            this.renderTarget.GetData<Color>(collisionData);

            return worm.Collision(collisionData, this.renderTarget.Height, this.renderTarget.Width);                
        }
        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {

            this.GraphicsDevice.SetRenderTarget(this.renderTarget);

            this.spriteBatch.Begin();
            this.spriteBatch.Draw(this.pixel, this.worm.Bounds, null, Color.GreenYellow, 0, this.worm.Origin, SpriteEffects.None, 0);
            this.spriteBatch.End();

            this.GraphicsDevice.SetRenderTarget(null);


            this.spriteBatch.Begin();
            this.spriteBatch.Draw(this.renderTarget, this.GraphicsDevice.Viewport.Bounds, Color.White);
            this.spriteBatch.End();
            
            this.spriteBatch.Begin();
            lock (_lock)
            {
                for(int i = 0; i < this.players.Count; i++)
                {
                    Vector2 offset = new Vector2(20, i * 50);
                    if(players[i].Name != null)
                        this.spriteBatch.DrawString(this.font, players[i].Name, offset, players[i].Color);
                }
            }
            this.spriteBatch.End();
        }
    }
}
