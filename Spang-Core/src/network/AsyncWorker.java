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
package network;

/**
 * Base class to workers that does work concurrently in a worker thread.
 *
 *  This class and all sub-classes are designer to work in this way.
 *<pre>	//Creation
 *		AsyncWorkerSub worker = new AsyncWorkerSub();
 *  	Thread thread = new Thread(worker);
 *  	thread.start();
 *  
 *  	//Termination
 *  	worker.stopWorking();
 *  	  
 *</pre>
 *  
 * @author Lukas Kurtyan
 */
public abstract class AsyncWorker implements Runnable {
	protected volatile boolean stopWorking;
	
	/**
	 * This does any actual work.
	 */
	protected abstract void DoWork();
	
	/**
	 * This method signals the worker thread that it should stop working.
	 */
	public void StopWorking() {
		this.stopWorking = true;
	}
	
	/**	
	 * {@inheritDoc}
	 */
	public void run() {
		while(!stopWorking) {
			this.DoWork();			
		}
	}
	
}
