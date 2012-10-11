using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang_PC_C_sharp.TouchManager.States;

namespace Spang.Core.Touch
{
    class TouchStateMachine
    {
        private TouchState state;

        public TouchStateMachine(TouchEventManager eventManager)
        {
            this.state = new NullState(this, eventManager);
        }

        public void ChangeState(TouchState newState, TouchEvent te)
        {
            Console.WriteLine("{0} to {1}", state, newState);

            if (this.state != null)
                this.state.Exit(te);

            this.state = newState;
            this.state.Enter(te);
        }

        public void Update(TouchEvent touchEvent)
        {
            this.state.Update(touchEvent);
        }
    }
}
