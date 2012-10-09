using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang_PC_C_sharp.Touch_Manager;

namespace Spang_PC_C_sharp.TouchManager.States
{
    class MultiDownState : TouchState
    {

        public MultiDownState(TouchStateMachine machine, TouchEventManager manager)
            : base(machine, manager) { }

        internal override void Enter(TouchEvent touchEvent)
        {
            throw new NotImplementedException();
        }

        internal override void Exit(TouchEvent touchEvent)
        {
            throw new NotImplementedException();
        }

        internal override void Update(TouchEvent touchEvent)
        {
            throw new NotImplementedException();
        }
    }
}
