/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
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
