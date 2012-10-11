using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using Spang.Core.Android;

namespace Spang_PC_C_sharp
{
    class DesktopController
    {
        private readonly IPhone phone;
        private readonly OsInterface os;

        public DesktopController(IPhone phone, OsInterface os)
        {
            this.phone = phone;
            this.os = os;
            this.RegisterEvents();
        }

        private void RegisterEvents()
        {
            phone.Tapped += LeftClick;
            phone.LongTapped += RightClick;
            phone.TouchMoved += MoveMouse;
            phone.VolumeUp += IncreaseVolume;
            phone.VolumeDown += DecreaseVolume;
            phone.VerticalScroll += VerticalScroll;
            phone.Horizontalscroll += HorizontalScroll;
            phone.NetworkedText += NetworkedText;
        }

        public void LeftClick()
        {
            os.SendMouseEvent(MouseEvent.LeftDown);
            os.SendMouseEvent(MouseEvent.LeftUp);
        }

        public void RightClick()
        {
            os.SendMouseEvent(MouseEvent.RightDown);
            os.SendMouseEvent(MouseEvent.RightUp);
        }

        public void mouseDown()
        {
            os.SendMouseEvent(MouseEvent.LeftDown);
        }

        public void mouseUp()
        {
            os.SendMouseEvent(MouseEvent.LeftUp);
        }

        public void VerticalScroll(int delta)
        {
            os.SendMouseEvent(MouseEvent.MouseWheel, delta, 0);
        }

        public void HorizontalScroll(int delta)
        {
            os.SendMouseEvent(MouseEvent.MouseHWheel, delta, 0);
        }

        public void MoveMouse(int dx, int dy)
        {
            Cursor.Position = new Point(Cursor.Position.X - dx, Cursor.Position.Y - dy);
        }

        public void IncreaseVolume()
        {
            os.IncreaseVolume();
        }

        public void DecreaseVolume()
        {
            os.DecreaseVolume();
        }

        public void NetworkedText(string text)
        {
            foreach (var item in text)
            {
                os.SendKey(item);
            }
        }

    }
}
