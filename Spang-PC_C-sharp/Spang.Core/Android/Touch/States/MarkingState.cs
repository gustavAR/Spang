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
using System.Diagnostics;

namespace Spang.Core.Android
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
