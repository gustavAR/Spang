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

namespace Spang.Core.Android
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
