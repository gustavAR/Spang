package sensors;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import network.IConnection;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * Class used to communicate sensor input with other parts of the system.
 * @author Pontus Pall & Gustav Alm Rosenblad
 */
public class SensorProcessor extends Service{

	private static int DEFAULT_SAMPLINGRATE = 20;
	private SharedPreferences preferences;
	private List<ISensor> sensors = new ArrayList<ISensor>();
	private ByteBuffer encodedSensorInput; 
	private IConnection connection;
	private SensorManager manager;
	private int samplingRate;


	@Override
	public void onCreate() {
		super.onCreate();

		this.manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE); 

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		SensorListBuilder builder = new SensorListBuilder(this.manager);
		this.sensors = builder.build();
		this.connection = connection;	
		this.encodedSensorInput = ByteBuffer.allocate(getOutputLength()).order(ByteOrder.LITTLE_ENDIAN);

		

	}

	/**
	 * Starts a thread which processes the input from all active sensors 
	 * with a specific sampling rate.
	 */
	public void startProcess() {
		Timer timer = new Timer();
		for (final ISensor sensor : sensors) {
			TimerTask task = new TimerTask() {

				@Override
				public void run() {	
					processInput(sensor);
				}
			};
			timer.scheduleAtFixedRate(task, 0, getSamplingRateBySensor(sensor));
		}
	}

	private int getSamplingRateBySensor(ISensor sensor){	
		return this.preferences.getInt(""+sensor.getSensorID(), DEFAULT_SAMPLINGRATE);
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
	 * Updates the value of getEncodedSensorInput() for the specified sensor
	 * @param sensor 
	 */
	private void processInput(ISensor sensor) {
		fillOutput(sensor);
		this.connection.sendUDP(encodedSensorInput.array());
		encodedSensorInput.clear();
	}

	private void fillOutput(ISensor sensor) {	
		if(sensor.isRunning()) {
			sensor.encode(this.encodedSensorInput);		
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

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}