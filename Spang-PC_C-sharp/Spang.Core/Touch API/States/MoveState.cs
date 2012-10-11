using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Touch
{
    class MoveState : TouchState
    {
        private Touch prevPointer;

        public MoveState(TouchStateMachine touchStateMachine, TouchEventManager touchEventManager)
            : base(touchStateMachine, touchEventManager)
        {
        }

        internal override void Enter(TouchEvent touchEvent)
        {
            this.prevPointer = touchEvent.Touches[0];
        }

        internal override void Exit(TouchEvent touchEvent)
        {
        }

        internal override void Update(TouchEvent touchEvent)
        {
            if (touchEvent.Touches.Count == 1)
            {
                Touch currPointer = touchEvent.Touches[0];
                this.manager.OnMove((int)(this.prevPointer.Location.X - currPointer.Location.X),
                                    (int)(this.prevPointer.Location.Y - currPointer.Location.Y));
                this.prevPointer = currPointer;
            }
            else if (touchEvent.Touches.Count == 0)
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
