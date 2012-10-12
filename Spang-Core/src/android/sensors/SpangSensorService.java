package android.sensors;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Timer;
import java.util.TimerTask;

import network.exceptions.NotImplementedException;
import network.messages.SensorEvent;

import org.apache.http.MalformedChunkCodingException;

import utils.MissingSensorException;
import utils.Packer;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.network.NetworkService;
import android.network.NetworkService.NetworkBinder;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.AndroidRuntimeException;

/**
 * Class used to communicate sensor input with other parts of the system.
 * Sensors can be activated by sending samplingrate integers as intent extras with
 * a string key corresponding to the specific sensor 
 * (e.g. the string SpangSensorService.ACCELEROMETER_EXTRA will identify the sample rate
 * of the accelerometer sensor). 
 * Any samplingrate > 0 will also make the sensor activated.
 * 
 * @author Pontus Pall, Lukas Kurtyan, Joakim Johansson & Gustav Alm Rosenblad
 */
public class SpangSensorService extends Service{

	public static String ACCELEROMETER_EXTRA	= "spang.spang_sensor_service.accelerometer";
	public static String GYROSCOPE_EXTRA		= "spang.spang_sensor_service.gyroscope";
	public static String LUMINANCE_EXTRA		= "spang.spang_sensor_service.luminance";
	public static String MAGNETICFIELD_EXTRA	= "spang.spang_sensor_service.magneticField";
	public static String HUMIDITY_EXTRA			= "spang.spang_sensor_service.humidity";
	public static String PROXIMITY_EXTRA		= "spang.spang_sensor_service.proximity";
	public static String AIRPRESSURE_EXTRA		= "spang.spang_sensor_service.airPressure";
	public static String GRAVITY_EXTRA			= "spang.spang_sensor_service.gravity";
	public static String ORIENTATION_EXTRA		= "spang.spang_sensor_service.orientation";
	
	
	private static int DEFAULT_SAMPLINGRATE = 20;
	private SharedPreferences preferences;
	private List<ISensor> sensors = new ArrayList<ISensor>();
	private NetworkService networkService;
	private SensorManager manager;
	private int samplingRate;
	private Timer timer;
	private IBinder binder = new SpangSensorBinder();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		this.timer = new Timer();
		this.manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE); 

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				onPreferenceChanged(sharedPreferences, key);
				
			}
		});

		Resources resources = this.getResources();

		SensorListBuilder builder = new SensorListBuilder(this.manager, resources);
		this.sensors = builder.build();
	}
	
	public void onPreferenceChanged(SharedPreferences sharedPreferences, String key){
		this.stopProcess();
		this.startProcess();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation") //Orientation sensor is deprecated, but we only use the sensor type value here.
									 //The ISensor used in the service uses non-depricated methods.
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent i = new Intent(this, NetworkService.class);
		this.bindService(i, this.connection, Context.BIND_WAIVE_PRIORITY);
		
		setUpSensorFromIntent(intent, ACCELEROMETER_EXTRA, 	Sensor.TYPE_ACCELEROMETER);
		setUpSensorFromIntent(intent, GYROSCOPE_EXTRA, 		Sensor.TYPE_GYROSCOPE);
		setUpSensorFromIntent(intent, LUMINANCE_EXTRA, 		Sensor.TYPE_LIGHT);
		setUpSensorFromIntent(intent, MAGNETICFIELD_EXTRA, 	Sensor.TYPE_MAGNETIC_FIELD);
		setUpSensorFromIntent(intent, PROXIMITY_EXTRA,	 	Sensor.TYPE_PROXIMITY);
		setUpSensorFromIntent(intent, HUMIDITY_EXTRA, 		Sensor.TYPE_RELATIVE_HUMIDITY);
		setUpSensorFromIntent(intent, AIRPRESSURE_EXTRA, 	Sensor.TYPE_PRESSURE);
		setUpSensorFromIntent(intent, GRAVITY_EXTRA, 		Sensor.TYPE_GRAVITY);
		setUpSensorFromIntent(intent, ORIENTATION_EXTRA, 	Sensor.TYPE_ORIENTATION); 
		
		return START_STICKY;
	}
	
	/**
	 * Helping method setting the sample rate of a specified sensor based on an
	 * intent. 
	 * If the sample rate is < 0, the sensor is not added.
	 * @param intent The intent containing the extras. Should be the intent used to
	 * start the spangSensorService
	 * @param intentExtraKey the string key corresponding to the specific sensor 
	 * (see javadoc of SpangSensorService).
	 * @param SensorType Integer representing the specific sensor (e.g. Sensor.TYPE_MAGNETIC_FIELD)
	 */
	private void setUpSensorFromIntent(Intent intent, String intentExtraKey, int SensorType){
		int sampleRate= intent.getIntExtra(intentExtraKey, -1);
		if(sampleRate > 0)
			this.addSensor(SensorType, sampleRate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unbindService(connection);
	}

	//Connection used to bind the network service.
	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			stopProcess();
			networkService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			networkService = ((NetworkService.NetworkBinder)service).getService();

			startProcess();
		}
	};

	/**
	 * Starts processing input.
	 * Creates a new timer and schedules one task per sensor, processing the sensor
	 * input at a fixed sampling rate.
	 */
	public void startProcess() {
		for (final ISensor sensor : sensors) {
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					if(isSensorActivated(sensor)){
						sensor.stop();
						sensor.start();
						processInput(sensor);
					}
					else
						sensor.stop();
				}
			};
			timer.scheduleAtFixedRate(task, 0, getSamplingRateBySensor(sensor));
		}
	}
	/**
	 * Stops the processing of the sensor input.
	 */
	public void stopProcess() {
		this.timer.cancel();
	}

	private int getSamplingRateBySensor(ISensor sensor){	
		return 1000 / (this.preferences.getInt("sampleRate"+sensor.getName(), DEFAULT_SAMPLINGRATE) + 1);
	}

	private boolean isSensorActivated(ISensor sensor){
		return this.preferences.getBoolean("isActivated"+sensor.getName(), true);
	}

	/**
	 * Updates the value of getEncodedSensorInput() for the specified sensor
	 * @param sensor 
	 */
	private void processInput(ISensor sensor) {
		if(this.networkService == null || !this.networkService.isConnected())
			return;
		
		SensorEvent event = new SensorEvent(sensor.getSensorID(), sensor.getValues());
		this.networkService.send(event);		
	}
	
	/**
	 * Adds a sensor of type sensorID.
	 * These types can be found in the Sensor.TYPE_<sensor name> int code. 
	 * (e.g. Sensor id of accelerometer is Sensor.TYPE_ACCELEROMETER). 
	 * @param sensorID 
	 * @param sampleRate The rate to update the sensor values
	 * @throws MissingResourceException if the sensor is not available on the device. 
	 */
	public void addSensor(int sensorID, int sampleRate) throws MissingSensorException {
		ISensor temp = null;
		for (ISensor s: this.sensors) {
			if(s.getSensorID() == sensorID){
				temp=s;
				break;
			}
		}
		if(temp == null)
			throw new MissingSensorException("No sensor found with ID:"+sensorID);
		
		final ISensor sensor = temp;
		sensor.start();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
			//	if(isSensorActivated(sensor)){
			//		sensor.stop();
			//		sensor.start();
					processInput(sensor);
			//	}
			//	else
			//		sensor.stop();
			}
		};
		timer.scheduleAtFixedRate(task, 0, sampleRate);		
	}
	
	public void removeSensor(int sensorID) {
		//TODO Implement
		throw new NotImplementedException("removeSensor not implementet yet...");
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

	
	public class SpangSensorBinder extends Binder {
		
		public SpangSensorService getService()
		{
			return SpangSensorService.this;
		}
	
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}
}