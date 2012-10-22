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
package spang.android.sensors;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Timer;
import java.util.TimerTask;

import network.messages.SensorEvent;
import spang.android.network.NetworkService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

/**
 * Class used to communicate sensor input with other parts of the system.
 * Sensors can be activated by sending sampling rate integers as intent extras with
 * a string key corresponding to the specific sensor 
 * (e.g. the string SpangSensorService.ACCELEROMETER_EXTRA will identify the sample rate
 * of the accelerometer sensor). 
 * Any sampling rate > 0 will also make the sensor activated.
 * 
 * @author Pontus Pall, Lukas Kurtyan, Joakim Johansson & Gustav Alm Rosenblad
 */
public class SpangSensorService extends Service{

	public static final String ACCELEROMETER_EXTRA	= "spang.spang_sensor_service.accelerometer";
	public static final String GYROSCOPE_EXTRA		= "spang.spang_sensor_service.gyroscope";
	public static final String LUMINANCE_EXTRA		= "spang.spang_sensor_service.luminance";
	public static final String MAGNETICFIELD_EXTRA	= "spang.spang_sensor_service.magneticField";
	public static final String HUMIDITY_EXTRA		= "spang.spang_sensor_service.humidity";
	public static final String PROXIMITY_EXTRA		= "spang.spang_sensor_service.proximity";
	public static final String AIRPRESSURE_EXTRA	= "spang.spang_sensor_service.airPressure";
	public static final String GRAVITY_EXTRA		= "spang.spang_sensor_service.gravity";
	public static final String ORIENTATION_EXTRA	= "spang.spang_sensor_service.orientation";
	
	private List<ISensor> sensors = new ArrayList<ISensor>();
	private NetworkService networkService;
	private SensorManager manager;
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

		Resources resources = this.getResources();

		SensorListBuilder builder = new SensorListBuilder(this.manager, resources);
		this.sensors = builder.build();
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
			networkService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			networkService = ((NetworkService.NetworkBinder)service).getService();
		}
	};
	
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
					processInput(sensor);
			}
		};
		timer.scheduleAtFixedRate(task, 0, sampleRate);		
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