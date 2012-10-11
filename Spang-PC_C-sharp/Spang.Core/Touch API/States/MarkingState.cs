﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace Spang.Core.Touch
{
    class MarkingState : TouchState
    {
        private Touch prevPointer;

        public MarkingState(TouchStateMachine touchStateMachine, TouchEventManager touchEventManager)
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
            if (touchEvent.Touches.Count > 0)
            {
                Move(touchEvent);
            }
            else
            {
                this.manager.OnUp();
                this.machine.ChangeState(new NullState(this.machine, this.manager), touchEvent);
            }
        }

        private void Move(TouchEvent touchEvent)
        {
            Touch currPointer = touchEvent.Touches[0];
            this.manager.OnMove((int)(this.prevPointer.Location.X - currPointer.Location.X),
                                (int)(this.prevPointer.Location.Y - currPointer.Location.Y));
            this.prevPointer = currPointer;
        }
    }
}
