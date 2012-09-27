package sensors;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import network.IConnection;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

/**
 * Class used to communicate sensor input with other parts of the system.
 * @author Pontus Pall & Gustav Alm Rosenblad
 */
public class SensorProcessor {
	private static final String  SAMPLING_RATE_NAME = "SAMPLING_RATE";
	private static final int  DEFAULT_SAMPLING_RATE = 20;
	public static final String PREFS_NAME = "PreferenceFile";
	
	private SharedPreferences settings;
	private List<ISensor> sensors = new ArrayList<ISensor>();
	private ByteBuffer encodedSensorInput; 
	private IConnection connection;
	private SensorManager manager;
	private int samplingRate;

	public SensorProcessor(Context context, IConnection connection) {
		this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); 
		SensorListBuilder builder = new SensorListBuilder(this.manager);
		this.sensors = builder.build();
		this.connection = connection;	
		this.encodedSensorInput = ByteBuffer.allocate(getOutputLength()).order(ByteOrder.LITTLE_ENDIAN);
		
		this.settings = context.getSharedPreferences(PREFS_NAME, 0);
		this.samplingRate = settings.getInt(SAMPLING_RATE_NAME, DEFAULT_SAMPLING_RATE);
	}

	/**
	 * Starts a thread which processes the input from all active sensors 
	 * with a specific sampling rate.
	 */
	public void startProcess() {
		Runnable runnable = new Runnable() {
			public void run() {
				while(true){
					processInput();
					try {
						Thread.sleep(samplingRate);
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
		this.encodedSensorInput = null;
		this.encodedSensorInput = ByteBuffer.allocate(getOutputLength()).order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Updates the value of getEncodedSensorInput();
	 */
	private void processInput() {
		fillOutput();
		this.connection.sendUDP(encodedSensorInput.array());
		encodedSensorInput.clear();
	}

	private void fillOutput() {
		for (ISensor sensor : sensors) {
			if(sensor.isRunning()) {
				sensor.encode(this.encodedSensorInput);		
			}
		}
	}

	/**
	 * @return the length of the output of all used sensors.
	 */
	private int getOutputLength() {
		int totalEncodedLength = 0;

		for (ISensor sensor : sensors) {
			if(sensor.isRunning()) {
				totalEncodedLength += sensor.getEncodedLength();
			}
		}
		return totalEncodedLength;
	}

	/**
	 * @return The sampling rate of the processor. 
	 */
	public int getSamplingRate() {
		return samplingRate;
	}

	/**
	 * Sets the sampling rate to a new value.
	 * @param samplingRate the new sampling rate.
	 */
	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}
}