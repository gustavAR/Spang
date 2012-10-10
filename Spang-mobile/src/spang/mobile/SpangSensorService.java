package spang.mobile;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Timer;
import java.util.TimerTask;

import sensors.ISensor;
import sensors.SensorListBuilder;
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
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.AndroidRuntimeException;

/**
 * Class used to communicate sensor input with other parts of the system.
 * @author Pontus Pall, Joakim Johansson & Gustav Alm Rosenblad
 */
public class SpangSensorService extends Service{

	public static String LUMINANCE_EXTRA = "spang.spang_sensor_service.luminance";
	
	
	
	private static int DEFAULT_SAMPLINGRATE = 20;
	private SharedPreferences preferences;
	private List<ISensor> sensors = new ArrayList<ISensor>();
	private Packer encodedSensorInput; 
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
		this.encodedSensorInput = new Packer();

	}
	public void onPreferenceChanged(SharedPreferences sharedPreferences, String key){
		this.stopProcess();
		this.startProcess();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent i = new Intent(this, NetworkService.class);
		this.bindService(i, this.connection, Context.BIND_WAIVE_PRIORITY);
		
		int luminanceSamplerFPS = intent.getIntExtra(LUMINANCE_EXTRA, -1);
		if(luminanceSamplerFPS != -1) {
			this.addSensor(Sensor.TYPE_LIGHT, luminanceSamplerFPS);
		}
		
		
		return START_STICKY;
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
		this.timer = new Timer();
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
		fillOutput(sensor);
		this.networkService.send(encodedSensorInput.getPackedData());
		encodedSensorInput.clear();
	}

	private void fillOutput(ISensor sensor) {	 
		sensor.encode(this.encodedSensorInput);		
	}
	
	/**
	 * Adds a sensor of type sensorID.
	 * These types can be found in the Sensor.TYPE_STUFF int code. For 
	 * ex. Sensor id of accelerometer is Sensor.TYPE_ACCELEROMETER. 
	 * @param sensorID 
	 * @param samplerRate
	 * @throws MissingResourceException if the sensor is not avalible on the device. 
	 */
	public void addSensor(int sensorID, int samplerRate) throws MissingResourceException {
		//Balwhdanwldalwjdaljwdliawjdliawjdlijawdlijlawjid		
	}
	
	public void removeSensor(int sensorID) {
		
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