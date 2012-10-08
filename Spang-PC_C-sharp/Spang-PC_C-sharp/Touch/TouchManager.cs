﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class TouchEventManager
    {
        public event Action Tap;
        public event Action LongTap;
        public event Action Down;
        public event Action Up;
        
        public event Action<int, int> Move;

        public event Action<int> MultiTap;
        public event Action<int, int, int> MulitiMove;


        internal void OnTap()
        {
            if (this.Tap != null)
                this.Tap();
        }

        internal void OnLongTap()
        {
            if (this.LongTap != null)
                this.LongTap();
        }

        internal void OnDown()
        {
            if (this.Down!= null)
                this.Down();
        }

        internal void OnUp()
        {
            if (this.Up != null)
                this.Up();
        }

        internal void OnMove(int dx, int dy)
        {
            if (this.Move != null)
                this.Move(dx, dy);
        }

        internal void OnMultiTap(int count)
        {
            if (this.MultiTap != null)
                this.MultiTap(count);
        }

        internal void OnMultiMove(int count, int dx, int dy)
        {
            if (this.MulitiMove != null)
                this.MulitiMove(count, dx,dy);
        }
    }
}
