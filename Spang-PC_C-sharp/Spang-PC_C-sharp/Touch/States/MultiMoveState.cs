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
            if (touchEvent.Pointers.Count == 1)
            {
                this.machine.ChangeState(new MoveState(this.machine, this.manager), touchEvent);
            }
            else if (touchEvent.Pointers.Count > 0)
            {
                int count = Math.Min(this.prevEvent.Pointers.Count, touchEvent.Pointers.Count);
                int x = 0, y = 0;
                for (int i = 0; i < count; i++)
                {
                    Vector3 prev = this.prevEvent.Pointers[i];
                    Vector3 curr = touchEvent.Pointers[i];
                    x = (int)(prev.X - curr.X);
                    y = (int)(prev.Y - curr.Y);

                    this.manager.OnMultiMove(count, x, y);
                }
            }
            else
            {
                this.machine.ChangeState(new NullState(machine, manager), null);
            }

            this.prevEvent = touchEvent;
        }
    }
}
