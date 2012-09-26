package network;

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
	
	public void run() {
		while(!stopWorking) {
			this.DoWork();			
		}
	}
	
}
