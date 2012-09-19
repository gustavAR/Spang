package sensors;
import java.util.ArrayList;
import java.util.List;

import network.IConnection;
import android.content.Context;
import android.util.Log;

/**
 * Class used to communicate sensor input with other parts of the system.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public class SensorProcessor {
	private List<SpangSensor> sensors = new ArrayList<SpangSensor>();
	private byte[] encodedSensorInput; 
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
				processInput();
				try {
					Thread.sleep(167);
				} catch (InterruptedException e) {
					e.printStackTrace();
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
		for (SpangSensor sensor : sensors) {
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
		byte[] output = new byte[getOutputLength()];
		fillOutput(output);
		
		this.encodedSensorInput = output;
		this.connection.sendUDP(output);
		Log.d("Sensor-output:", output.toString());
	}

	private void fillOutput(byte[] output) {
		int outputIndex = 0;
		
		for (SpangSensor sensor : sensors) {
			if(sensor.isRunning()) {
				byte[] encodedValues = sensor.encode();		
				for(int i = 0; i < encodedValues.length; i++) {
					output[outputIndex] = encodedValues[i];
					outputIndex++;
				}
			}
		}
	}

	private int getOutputLength() {
		int totalEncodedLength = 0;
		
		for (SpangSensor sensor : sensors) {
			if(sensor.isRunning()) {
				totalEncodedLength += sensor.getEncodedLength();
			}
		}
		return totalEncodedLength;
	}

	/**
	 * @return an array containing all the encoded sensor values.
	 */
	public byte[] getEncodedSensorInput() {
		return encodedSensorInput;
	}
}