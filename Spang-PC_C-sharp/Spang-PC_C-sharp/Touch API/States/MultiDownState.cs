using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang_PC_C_sharp.Touch_Manager;
using System.Timers;

namespace Spang_PC_C_sharp.TouchManager.States
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
