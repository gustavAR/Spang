﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    abstract class TouchState
    {
        protected TouchStateMachine machine;
        protected TouchEventManager manager;

        public TouchState(TouchStateMachine stateMachine, TouchEventManager manager)
        {
            this.machine = stateMachine;
            this.manager = manager;
        }

        internal abstract void Enter(TouchEvent touchEvent);
        internal abstract void Exit(TouchEvent touchEvent);
        internal abstract void Update(TouchEvent touchEvent);

        public override string ToString()
        {
            return this.GetType().Name;
        }
    }
}
