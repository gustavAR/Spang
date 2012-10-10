using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp.TouchManager.States
{
    class MultiMoveState : TouchState
    {
        private TouchEvent prevEvent;

        public MultiMoveState(TouchStateMachine touchStateMachine, TouchEventManager touchEventManager)
            : base(touchStateMachine , touchEventManager)
        {
        }

        internal override void Enter(TouchEvent touchEvent)
        {
            this.prevEvent = touchEvent;
        }

        internal override void Exit(TouchEvent touchEvent)
        {
        }

        internal override void Update(TouchEvent touchEvent)
        {
            if (touchEvent.Touches.Count == 1)
            {
                this.machine.ChangeState(new MoveState(this.machine, this.manager), touchEvent);
            }
            else if (touchEvent.Touches.Count == 2)
            {
                int delta;
                if (IsPinch(touchEvent, out delta))
                {
                    this.manager.OnPinch(delta);
                }
                else
                {
                    Move(touchEvent);
                }
            }
            else if (touchEvent.Touches.Count > 0)
            {
                Move(touchEvent);
            }
            else
            {
                this.machine.ChangeState(new NullState(machine, manager), null);
            }

            this.prevEvent = touchEvent;
        }


        private bool IsPinch(TouchEvent touchEvent, out int delta)
        {
            Vector2 prevA = this.prevEvent.Touches[0].Location;
            Vector2 prevB = this.prevEvent.Touches[1].Location;

            Vector2 currA = touchEvent.Touches[0].Location;
            Vector2 currB = touchEvent.Touches[1].Location;

            float doted0 = Vector2.Dot(prevA - currA, prevB - currB);
            float doted1 = Vector2.Dot(prevA - prevB, prevA - currA);

            if(doted1 + 0.1 > 0 && doted1 - 0.1 < 0 || 
               doted1 + 0.1 > Math.PI && doted1 - 0.1 < Math.PI) 
            {
                float dist0 = Vector2.Distance(prevA, prevB);
                float dist1 = Vector2.Distance(currA, currB);

                delta = (int)(dist1 - dist0);
                return true;
            }

            delta = 0;
            return false;
        }

        private void Move(TouchEvent touchEvent)
        {
            int count = Math.Min(this.prevEvent.Touches.Count, touchEvent.Touches.Count);
            int x = 0, y = 0;
            for (int i = 0; i < count; i++)
            {
                Vector2 prev = this.prevEvent.Touches[i].Location;
                Vector2 curr = touchEvent.Touches[i].Location;
                x = (int)(prev.X - curr.X);
                y = (int)(prev.Y - curr.Y);

                this.manager.OnMultiMove(count, x, y);
            }
        }
    }
}
