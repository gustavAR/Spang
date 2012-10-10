﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang_PC_C_sharp.Touch_Manager;
using System.Timers;
using System.Diagnostics;

namespace Spang_PC_C_sharp.TouchManager.States
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