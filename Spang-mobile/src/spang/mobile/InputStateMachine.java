package spang.mobile;

import android.view.MotionEvent;

public class InputStateMachine extends StateMachine<InputState>{
	
	public void onTouchEvent(MotionEvent event){
		this.activeState.onTouchEvent(event);
	}
}
