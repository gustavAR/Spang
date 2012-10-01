package network;

/**
 * Base class to workers that does work concurrently in a worker thread.
 *
 *  This class and all sub-classes are designer to work in this way.
 *<pre>	//Creation
 *		ContinuousWorkerSub worker = new ContinuousWorkerSub();
 *  	Thread thread = new Thread(worker);
 *  	thread.start();
 *  	//Termination
 *  	worker.stopWorking();
 *</pre>
 *  
 * @author Lukas Kurtyan
 */
public abstract class ContinuousWorker implements Runnable {
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
