using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace Spang_PC_C_sharp
{
    class DesktopController
    {
        private readonly Phone phone;

        public DesktopController(Phone phone)
        {
            this.phone = phone;
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

        private void LeftClick()
        {
            MouseEventSender.SendEvent(MouseEvent.LeftDown);
            MouseEventSender.SendEvent(MouseEvent.LeftUp);
        }

        public void RightClick()
        {
            MouseEventSender.SendEvent(MouseEvent.RightDown);
            MouseEventSender.SendEvent(MouseEvent.RightUp);
        }

        public void VerticalScroll(int delta)
        {
            MouseEventSender.SendEvent(MouseEvent.MouseWheel, delta, 0);
        }

        public void HorizontalScroll(int delta)
        {
            MouseEventSender.SendEvent(MouseEvent.MouseHWheel, delta, 0);
        }

        public void MoveMouse(int dx, int dy)
        {
            Cursor.Position = new Point(Cursor.Position.X - dx, Cursor.Position.Y - dy);
        }

        public void IncreaseVolume()
        {
            VolumeChanger.IncreaseVolume();
        }

        public void DecreaseVolume()
        {
            VolumeChanger.DecreaseVolume();
        }

        public void NetworkedText(string text)
        {
            foreach (var item in text)
            {
                KeyboardEventSender.SendKey(item);
            }
        }

    }
}
