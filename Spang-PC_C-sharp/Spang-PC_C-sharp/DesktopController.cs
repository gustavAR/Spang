﻿using System;
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
            phone.VolumeUp += IncreaseVolume;
            phone.VolumeDown += DecreaseVolume;
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
            KeyModifier modifiers = KeyModifier.None;

            for (int i = 0; i < text.Length; i++)
            {
                if (text[i] == '$' && text[i + 1] == '{')
                {
                    i += 2;//Now we are at the first modifier/function inside the modifier string
                    while (text[i] != '}')
                    {
                        switch (text[i])
                        {
                            case ('s'):
                                modifiers |= KeyModifier.Shift;
                                break;
                            case ('c'):
                                modifiers |= KeyModifier.Ctrl;
                                break;
                            case ('a'):
                                modifiers |= KeyModifier.Alt;
                                break;
                            case ('F'):
                                string toSend = "{";
                                while (text[i] != '}')
                                {
                                    toSend += text[i];
                                    i++;
                                }
                                toSend += "}";
                                os.SendKey(toSend, modifiers);
                                modifiers = KeyModifier.None;
                                continue;
                            default:
                                break;
                        }
                        i++;
                    }
                }
                else
                {
                    os.SendKey(text[i], modifiers);
                }
            }

        }

    }
}
