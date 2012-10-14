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
    public class TouchStateMachine
    {
        private TouchState state;

        public TouchStateMachine(TouchEventManager eventManager)
        {
            this.state = new NullState(this, eventManager);
        }

        internal void ChangeState(TouchState newState, TouchEvent te)
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
