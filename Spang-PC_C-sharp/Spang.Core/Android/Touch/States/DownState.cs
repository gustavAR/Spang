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
using System.Diagnostics;
using Spang.Core.Utils;

namespace Spang.Core.Android
{
    class DownState : TouchState
    {
        private static TimeSpan tapTime = TimeSpan.FromMilliseconds(400);
        private static TimeSpan longTapTime = TimeSpan.FromMilliseconds(2200);

        private Timer longTapTimer;
        private Stopwatch tapWatch;
        private Touch startPointer;


        public DownState(TouchStateMachine machine, TouchEventManager manager)
            : base(machine, manager) 
        {
            this.tapWatch = new Stopwatch();
            this.longTapTimer = new Timer(longTapTime.Milliseconds);
        }


        internal override void Enter(TouchEvent touchEvent)
        {
            this.startPointer = touchEvent.Touches[0];
            this.longTapTimer.AutoReset = false;
            longTapTimer.Elapsed += (s, e) =>
            {
                this.manager.OnLongTap();
                this.manager.OnDown();
                this.machine.ChangeState(new MarkingState(this.machine, this.manager), new TouchEvent(this.startPointer));
            };

            longTapTimer.Start();
            this.tapWatch.Start();
        }

        internal override void Exit(TouchEvent touchEvent)
        {
            this.longTapTimer.Stop();
            this.tapWatch.Stop();
        }

        internal override void Update(TouchEvent touchEvent)
        {
            if (touchEvent.Touches.Count == 0)
            {
                if (this.tapWatch.Elapsed <= tapTime)
                {
                    TransitionToTapState(touchEvent);
                }
                else
                {
                    TransitionToNullState(touchEvent);
                }
            }
            else if (touchEvent.Touches.Count == 1)
            {
                Touch pointer = touchEvent.Touches[0];
                if (Moved(pointer.Location))
                {
                    TransitionToMoveState(touchEvent, pointer);
                }
            }
            else
            {
                TranstionToMultiDownState(touchEvent);
            }
        }

        private bool Moved(Vector2 pointer)
        {
            return Math.Abs(pointer.X - this.startPointer.Location.X) > 5 ||
                   Math.Abs(pointer.Y - this.startPointer.Location.Y) > 5;

        }

        private void TranstionToMultiDownState(TouchEvent touchEvent)
        {
            this.machine.ChangeState(new MultiDownState(this.machine, this.manager), touchEvent);
        }

        private void TransitionToNullState(TouchEvent touchEvent)
        {
            this.machine.ChangeState(new NullState(this.machine, this.manager), touchEvent);
        }

        private void TransitionToTapState(TouchEvent touchEvent)
        {
            this.manager.OnTap();
            this.machine.ChangeState(new JustTappedState(this.machine, this.manager), touchEvent);
        }

        private void TransitionToMoveState(TouchEvent touchEvent, Touch pointer)
        {
            this.manager.OnMove((int)(this.startPointer.Location.X - pointer.Location.X), 
                                (int)(this.startPointer.Location.Y - pointer.Location.Y));
            this.machine.ChangeState(new MoveState(this.machine, this.manager), touchEvent);
        }
    }
}