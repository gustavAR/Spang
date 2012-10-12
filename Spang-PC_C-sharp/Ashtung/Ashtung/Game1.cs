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
    class Achtung : Microsoft.Xna.Framework.Game
    {
        public object _lock = new object();
        private const int PORT = 23452;

        public List<Player> players = new List<Player>();
        public SpriteFont font;
        public Texture2D pixel;
        public RenderTarget2D renderTarget;
        public Random random = new Random();
        public IServer server;


        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        
        GameScreen screen;

        public void ChangeScreen(GameScreen screen)
        {
            this.screen.Exit();
            this.screen = screen;
            this.screen.Enter();
        }

        public Achtung()
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
            this.GraphicsDevice.Clear(Color.Black);
            this.GraphicsDevice.SetRenderTarget(null);

            this.screen = new LobbyScreen(this);
            this.screen.Enter();


            this.font = Content.Load<SpriteFont>("SpriteFont1");
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
            this.screen.ConnectionDC(sender, eventArgs);
        }

        void MesssageRecived(IServer sender, RecivedEventArgs eventArgs)
        {
            if (eventArgs.Message is SensorEvent)
            {
                Console.WriteLine("Kind of got some SensorEvent");
            }
            this.screen.MessageRecived(sender, eventArgs);
        }

        void ConnectionRecived(IServer sender, ConnectionEventArgs eventArgs)
        {
            this.screen.ConnectionRecived(sender, eventArgs);
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
            lock (_lock)
            {
                this.screen.Update(gameTime);


            /*  
                }*/

            }

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            this.GraphicsDevice.Clear(Color.Black);

            lock (_lock)
            {
                this.screen.Draw(gameTime, this.spriteBatch);
            }
        }
    }
}
