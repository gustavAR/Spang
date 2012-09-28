package events;


/**
 *  A class that makes event-handling of Action1 simpler.
 *  This class is thread safe.
 * @author Lukas
 * @param <T> the type of argument the listener uses.
 */
public class Action1Delegate<T> extends Delegate<Action1<T>>{
	/**
	 * Creates the Action1Delegate.
	 */
	public Action1Delegate(){
		super();
	}
	
	/**
	 * Invokes the delegate making it notify all of it's listeners.
	 * @param args argument invoked with.
	 */
	public void invoke(T args) {
		for (Action1<T> listener : this.listeners) {
			listener.onAction(args);
		}
	}
}