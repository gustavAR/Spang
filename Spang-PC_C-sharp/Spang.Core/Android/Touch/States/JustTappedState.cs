using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Timers;

namespace Spang.Core.Android
{
    class JustTappedState : TouchState
    {
        private const int doubleTapTimeIntervall = 200;

        private Timer nextTapTimer;

        public JustTappedState(TouchStateMachine touchStateMachine, TouchEventManager touchEventManager)
            : base(touchStateMachine, touchEventManager)
        {
        }

        internal override void Enter(TouchEvent touchEvent)
        {
            nextTapTimer = new Timer(doubleTapTimeIntervall);
            nextTapTimer.AutoReset = false;
            nextTapTimer.Elapsed += (s, e) =>
            {
                this.machine.ChangeState(new NullState(this.machine, this.manager), null);
            };
            nextTapTimer.Start();
        }

        internal override void Exit(TouchEvent touchEvent)
        {
            nextTapTimer.Stop();
        }

        internal override void Update(TouchEvent touchEvent)
        {
            this.manager.OnDown();
            this.machine.ChangeState(new MarkingState(this.machine, this.manager), touchEvent);
        }
    }
}
