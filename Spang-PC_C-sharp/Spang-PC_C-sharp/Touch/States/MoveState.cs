using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp.TouchManager.States
{
    class MoveState : TouchState
    {
        private Vector3 prevPointer;

        public MoveState(TouchStateMachine touchStateMachine, TouchEventManager touchEventManager)
            : base(touchStateMachine, touchEventManager)
        {
        }

        internal override void Enter(TouchEvent touchEvent)
        {
            this.prevPointer = touchEvent.Pointers[0];
        }

        internal override void Exit(TouchEvent touchEvent)
        {
        }

        internal override void Update(TouchEvent touchEvent)
        {
            if (touchEvent.Pointers.Count == 1)
            {
                Vector3 currPointer = touchEvent.Pointers[0];
                this.manager.OnMove((int)(this.prevPointer.X - currPointer.X),
                                    (int)(this.prevPointer.Y - currPointer.Y));
                this.prevPointer = currPointer;
            }
            else if (touchEvent.Pointers.Count == 0)
            {
                this.machine.ChangeState(new NullState(machine, manager), null);
            }
            else
            {
                this.machine.ChangeState(new MultiMoveState(this.machine, this.manager), touchEvent);
            }
        }
    }
}
