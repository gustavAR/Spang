using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace Spang_PC_C_sharp.TouchManager.States
{
    class DownAfterTapState : TouchState
    {
        private Vector3 prevPointer;

        public DownAfterTapState(TouchStateMachine touchStateMachine, TouchEventManager touchEventManager)
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
            if (touchEvent.Pointers.Count > 0)
            {
                Vector3 currPointer = touchEvent.Pointers[0];
                this.manager.OnMove((int)(this.prevPointer.X - currPointer.X),
                                    (int)(this.prevPointer.Y - currPointer.Y));
                this.prevPointer = currPointer;
            }
            else
            {
                this.manager.OnUp();
                this.machine.ChangeState(new NullState(this.machine, this.manager), touchEvent);
            }
        }
    }
}
