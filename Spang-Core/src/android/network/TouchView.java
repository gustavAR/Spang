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
package android.network;

import network.Protocol;
import network.messages.Touch;
import network.messages.TouchEvent;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {

	private NetworkService service;

	public TouchView(Context context, NetworkService service) {
		super(context);
		this.service = service;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointers = event.getPointerCount();

		int eventID = event.getAction() & MotionEvent.ACTION_MASK;
		if(eventID == MotionEvent.ACTION_UP)
			pointers = 0;
		
		Touch[] touches = new Touch[pointers];
		for(int i = 0; i < pointers; i++) {
			float x = event.getX(i);
			float y = event.getY(i);
			float pressure = event.getPressure(i);
			touches[i] = new Touch(x,y, pressure);
		}
		
		service.sendDirect(new TouchEvent(touches), Protocol.Unordered);
		return true;
	}
}