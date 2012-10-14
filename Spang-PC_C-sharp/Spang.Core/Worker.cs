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
using System.Net.Sockets;

namespace Spang.Core
{
    /// <summary>
    /// A helper class making async tasks simpler.
    /// <remarks>This class does not do any work. Subclasses should themselfs determine what to do.</remarks>
    /// </summary>
    abstract class ContinuousWorker
    {
        //Used to stop doing work.
        //Since multible threads can acces it it is volatile.
        protected volatile bool stopWorking;

        /// <summary>
        /// Starts doing work.
        ///<remarks>This method should be invoked with the workerthread.</remarks> 
        /// </summary>
        public void DoWork()
        {
            Setup();
            while (!stopWorking)
            {
                DoWorkInternal();
            }
            Teardown();
        }
        
        /// <summary>
        /// Stop doing work. This can be called on any thread.
        /// <remarks>When this is called the working thread will exit as soon as possible.</remarks>
        /// </summary>
        public void StopWorking()
        {
            this.stopWorking = true;
        }

        /// <summary>
        /// Any setup needed should be done in this method.
        /// </summary>
        protected virtual void Setup() { }

        /// <summary>
        /// Any teardown needed should be done here.
        /// </summary>
        protected virtual void Teardown() { }

        /// <summary>
        /// This is where work should be done.
        /// </summary>
        protected abstract void DoWorkInternal();
    }
}
