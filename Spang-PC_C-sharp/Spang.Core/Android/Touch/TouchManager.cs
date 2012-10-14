/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Android
{
    public class TouchEventManager
    {
        private TouchStateMachine machine;

        public TouchEventManager()
        {
            this.machine = new TouchStateMachine(this);
        }

        public event Action Tap;
        public event Action LongTap;
        public event Action Down;
        public event Action Up;    
        public event Action<int, int> Move;
        public event Action<int> MultiTap;
        public event Action<int, int, int> MulitiMove;
        public event Action<int> Pinch;

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

        internal void OnPinch(int delta)
        {
            if (this.Pinch != null)
                this.Pinch(delta);
        }

        public void ProcessEvent(TouchEvent touchEvent)
        {
            this.machine.Update(touchEvent);
        }
    }
}
