using System;

namespace Ashtung
{
#if WINDOWS || XBOX
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            using (Achtung game = new Achtung())
            {
                game.Run();
            }
        }
    }
#endif
}

