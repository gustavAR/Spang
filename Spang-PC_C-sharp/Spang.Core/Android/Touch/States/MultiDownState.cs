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
using System.Timers;
using Spang.Core.Utils;

namespace Spang.Core.Android
{
    class MultiDownState : TouchState
    {
        private const int TAP_TIME = 400;

        private int pointerCount;
        private TouchEvent prevEvent;
        private Timer multiTapTimer;

        public MultiDownState(TouchStateMachine machine, TouchEventManager manager)
            : base(machine, manager) { }

        internal override void Enter(TouchEvent touchEvent)
        {
            this.prevEvent = touchEvent;
            this.pointerCount = touchEvent.Touches.Count;
            multiTapTimer = new Timer(TAP_TIME);
            multiTapTimer.AutoReset = false;
            multiTapTimer.Elapsed += (s,e) => 
            {
                this.machine.ChangeState(new MultiMoveState(this.machine, this.manager), this.prevEvent);
            };
            this.multiTapTimer.Start();
        }

        internal override void Exit(TouchEvent touchEvent)
        {
            this.multiTapTimer.Stop();
        }

        internal override void Update(TouchEvent touchEvent)
        {
            if (touchEvent.Touches.Count == 0)
            {
                this.manager.OnMultiTap(pointerCount);
                this.machine.ChangeState(new NullState(this.machine, this.manager), null);
            }
            else if (touchEvent.Touches.Count != prevEvent.Touches.Count)
            {
                if (touchEvent.Touches.Count > this.pointerCount)
                {
                    this.pointerCount = touchEvent.Touches.Count;
                }

                prevEvent = touchEvent;
            }
            else if (Moved(touchEvent))
            {
                Console.WriteLine("We moved!2");
                if (touchEvent.Touches.Count == 1)
                {
                    this.machine.ChangeState(new MoveState(this.machine, this.manager), touchEvent);
                }
                else
                {
                    this.machine.ChangeState(new MultiMoveState(this.machine, this.manager), touchEvent);
                }
            }
        }

        private bool Moved(TouchEvent touchEvent)
        {
            int count = Math.Min(touchEvent.Touches.Count, this.prevEvent.Touches.Count);

            for (int i = 0; i < count; i++)
            {
                if (Moved(touchEvent.Touches[i].Location, this.prevEvent.Touches[i].Location))
                    return true;
            }
            return false;
        }

        private bool Moved(Vector2 a, Vector2 b)
        {
            return Math.Abs(a.X - b.X) > 10 ||
                   Math.Abs(a.Y - b.Y) > 10;

        }
    }
}
