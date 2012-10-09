﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang_PC_C_sharp.Touch_Manager;

namespace Spang_PC_C_sharp.TouchManager.States
{
    class NullState : TouchState
    {
        public NullState(TouchStateMachine machine, TouchEventManager manager)
            : base(machine, manager) { }


        internal override void Enter(TouchEvent touchEvent)
        { }

        internal override void Exit(TouchEvent touchEvent)
        { }

        internal override void Update(TouchEvent touchEvent)
        {
            if (touchEvent.Touches.Count == 1)
            {
                this.machine.ChangeState(new DownState(this.machine, this.manager), touchEvent);
            }
            else
            {
                this.machine.ChangeState(new MultiDownState(this.machine, this.manager), touchEvent);
            }
        }
    }
}
