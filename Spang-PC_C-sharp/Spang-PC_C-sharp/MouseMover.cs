using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Windows.Forms;

namespace Spang_PC_C_sharp
{
    class MouseMover : IMessageHandler
    {
       
        public void Decode(BinaryReader reader)
        {
            int dx = reader.ReadInt32();
            int dy = reader.ReadInt32();

            Cursor.Position = new System.Drawing.Point(Cursor.Position.X - dx, Cursor.Position.Y - dy);

            Console.WriteLine("Moved Dx: {0} Dy: {1}", dx, dy);
        }
    }
}
