package sensors;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import network.IConnection;
import android.content.Context;

/**
 * Class used to communicate sensor input with other parts of the system.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public class SensorProcessor {
	private List<ISensor> sensors = new ArrayList<ISensor>();
	private ByteBuffer encodedSensorInput; 
	private IConnection connection;

	public SensorProcessor(Context context, IConnection connection) {
		SensorListBuilder builder = new SensorListBuilder(context);
		this.sensors = builder.build();
		this.connection = connection;	
	}

	/**
	 * Starts a thread which processes the input from all active sensors 
	 * approximately 60 times per second.
	 */
	public void startProcess() {
		Runnable runnable = new Runnable() {
			public void run() {
				while(true){
					processInput();
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(runnable).start();
	}

	/**
	 * Starts or stops a specific sensor.  
	 * @param sensorID the sensor to be started/stopped.
	 * @param value starts if true, stops if false.
	 */
	public void setActive(int sensorID, boolean value) {
		for (ISensor sensor : sensors) {
			if(sensor.getSensorID() == sensorID) {
				if(value){
					sensor.start();
					return;
				}
				sensor.stop();
				return;
			}
		}
	}

	/**
	 * Updates the value of getEncodedSensorInput();
	 */
	private void processInput() {
		this.encodedSensorInput = ByteBuffer.allocate(getOutputLength()).order(ByteOrder.LITTLE_ENDIAN);
		
		fillOutput();

		this.connection.sendUDP(encodedSensorInput.array());
	}

	private void fillOutput() {
		for (ISensor sensor : sensors) {
			if(sensor.isRunning()) {
				sensor.encode(this.encodedSensorInput);		
			}
		}
	}

	private int getOutputLength() {
		int totalEncodedLength = 0;

		for (ISensor sensor : sensors) {
			if(sensor.isRunning()) {
				totalEncodedLength += sensor.getEncodedLength();
			}
		}
		return totalEncodedLength;
	}
}