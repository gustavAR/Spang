package events;

public interface EventHandler<T1,T2> {

	public void onAction(T1 sender, T2 eventArgs);
}
