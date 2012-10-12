using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Decoding;
using Spang.Core.Utils;

namespace Spang.Core.Android
{
    public class AndroidPhone : Spang.Core.Android.IPhone 
    {
        private const int ACCELEROMETER_ID = 0;
        private const int LUMINANCE_ID = 1;
        private const int GYROSCOPE_ID = 2;
        private const int MAGNETIC_FIELD_ID = 3;
        private const int ORIENTATION_ID = 4;
        private const int PROXIMITY_ID = 5;
        private const int HUMIDITY_ID = 6;
        private const int AIR_PRESSURE_ID = 7;
        private const int GPS_ID = 8;
        private const int GRAVITY_ID = 9;


        private readonly TouchEventManager touchEventManager;

        public AndroidPhone()
        {
            this.touchEventManager = new TouchEventManager();
            this.touchEventManager.Tap += this.OnTap;
            this.touchEventManager.LongTap += this.OnLongTap;
            this.touchEventManager.Move += this.OnMove;
            this.touchEventManager.Down += this.OnDown;
            this.touchEventManager.Up += this.OnUp;
            this.touchEventManager.MultiTap += this.OnMultiTap;
            this.touchEventManager.MulitiMove += this.OnMultiMove;
            this.touchEventManager.Pinch += this.OnPinch;
       }

        #region Volume

        public event Action VolumeDown;
        internal void OnVolumeDown()
        {
            if (this.VolumeDown != null)
                this.VolumeDown();
        }
        

        public event Action VolumeUp;
        internal void OnVolumeUp()
        {
            if (this.VolumeUp != null)
                this.VolumeUp();
        }

        #endregion


        #region Accelerometer

        private Vector3 accelerometer;
        public Vector3 Accelerometer
        {
            get { return this.accelerometer; }

            set
            {
                Vector3 temp = this.accelerometer;
                this.accelerometer = value;
                if (temp != this.accelerometer)
                    this.OnAccelerometerChanged(temp, this.accelerometer);

            }
        }

        public event Action<Vector3, Vector3> AccelerometerChanged;
        private void OnAccelerometerChanged(Vector3 old, Vector3 current)
        {
            if (this.AccelerometerChanged != null)
                this.AccelerometerChanged(old, current);
        }

        #endregion

        #region Luminance

        private float luminance;
        public float Luminance 
        {
            get { return this.luminance; }
            internal set
            {
                float temp = this.luminance;
                this.luminance = value;

                if (temp != this.luminance)
                    this.OnLuminanceChanged(temp, this.luminance);
            }
        }

        public event Action<float, float> LuminanceChanged;
        private void OnLuminanceChanged(float old, float current)
        {
            if(this.LuminanceChanged != null)
                this.LuminanceChanged(old, current);
        }

        #endregion 

        #region Gyroscope

        private Vector3 gyroscope;
        public Vector3 Gyroscope
        {
            get { return this.gyroscope; }

            set
            {
                Vector3 temp = this.gyroscope;
                this.gyroscope = value;
                if (temp != this.gyroscope)
                    this.OnGyroscopeChanged(temp, this.gyroscope);

            }
        }

        public event Action<Vector3, Vector3> GyroscopeChanged;
        private void OnGyroscopeChanged(Vector3 old, Vector3 current)
        {
            if (this.GyroscopeChanged != null)
                this.GyroscopeChanged(old, current);
        }

        #endregion

        #region Magnetic field

        private Vector3 magneticField;
        public Vector3 MagneticField
        {
            get { return this.magneticField; }

            set
            {
                Vector3 temp = this.magneticField;
                this.magneticField = value;
                if (temp != this.magneticField)
                    this.OnMagneticFieldChanged(temp, this.magneticField);

            }
        }

        public event Action<Vector3, Vector3> MagneticFieldChanged;
        private void OnMagneticFieldChanged(Vector3 old, Vector3 current)
        {
            if (this.MagneticFieldChanged != null)
                this.MagneticFieldChanged(old, current);
        }


        #endregion

        #region Orientation

        private Vector3 orientation;
        public Vector3 Orientation
        {
            get { return this.orientation; }

            set
            {
                Vector3 temp = this.orientation;
                this.orientation = value;
                if (temp != this.orientation)
                    this.OnOrientationChanged(temp, this.orientation);

            }
        }

        public event Action<Vector3, Vector3> OrientationChanged;
        private void OnOrientationChanged(Vector3 old, Vector3 current)
        {
            if (this.OrientationChanged != null)
                this.OrientationChanged(old, current);
        }

        #endregion

        #region Proximity

        private float proximity;
        public float Proximity
        {
            get { return this.proximity; }
            internal set
            {
                float temp = this.proximity;
                this.proximity = value;

                if (temp != this.proximity)
                    this.OnProximityChanged(temp, this.proximity);
            }
        }

        public event Action<float, float> ProximityChanged;
        private void OnProximityChanged(float old, float current)
        {
            if (this.ProximityChanged != null)
                this.ProximityChanged(old, current);
        }

        #endregion

        #region Humidity

        private float humidity;
        public float Humidity
        {
            get { return this.humidity; }
            internal set
            {
                float temp = this.humidity;
                this.humidity = value;

                if (temp != this.humidity)
                    this.OnHumidityChanged(temp, this.humidity);
            }
        }

        public event Action<float, float> HumidityChanged;
        private void OnHumidityChanged(float old, float current)
        {
            if (this.HumidityChanged != null)
                this.HumidityChanged(old, current);
        }

        #endregion

        #region Pressure 

        private float pressure;
        public float Pressure
        {
            get { return this.pressure; }
            internal set
            {
                float temp = this.pressure;
                this.pressure = value;

                if (temp != this.pressure)
                    this.OnProximityChanged(temp, this.pressure);
            }
        }

        public event Action<float, float> PressureChanged;
        private void On(float old, float current)
        {
            if (this.PressureChanged != null)
                this.PressureChanged(old, current);
        }


        #endregion   

        #region GPS

        private Vector2 gpsLocation;
        public Vector2 GPSLocation
        {
            get { return this.gpsLocation; }

            set
            {
                Vector2 temp = this.gpsLocation;
                this.gpsLocation = value;
                if (temp != this.gpsLocation)
                    this.OnGPSLocationChanged(temp, this.gpsLocation);

            }
        }

        public event Action<Vector2, Vector2> GPSLocationChanged;
        private void OnGPSLocationChanged(Vector2 old, Vector2 current)
        {
            if (this.GPSLocationChanged != null)
                this.GPSLocationChanged(old, current);
        }

        #endregion

        #region Gravity

        private Vector3 gravity;
        public Vector3 Gravity
        {
            get { return this.gravity; }

            set
            {
                Vector3 temp = this.gravity;
                this.gravity = value;
                if (temp != this.gravity)
                    this.OnGravityChanged(temp, this.gravity);

            }
        }

        public event Action<Vector3, Vector3> GravityChanged;
        private void OnGravityChanged(Vector3 old, Vector3 current)
        {
            if (this.GravityChanged != null)
                this.GravityChanged(old, current);
        }

        #endregion


        public void ProcessMessage(IPhoneMessage iPhoneMessage)
        {
            if (iPhoneMessage is TouchEvent)
                this.OnTouch((TouchEvent)iPhoneMessage);
            else if (iPhoneMessage is SensorEvent)
            {
                this.OnSensor((SensorEvent)iPhoneMessage);
            }
        }

        private void OnSensor(SensorEvent sensorEvent)
        {
            float[] data = sensorEvent.SensorData;

            switch (sensorEvent.SensorID)
            {
                case ACCELEROMETER_ID:
                    this.Accelerometer = new Vector3(data[0], data[1], data[2]);
                    break;
                case LUMINANCE_ID:
                    this.Luminance = data[0];
                    break;
                case GYROSCOPE_ID:
                    this.Gyroscope = new Vector3(data[0], data[1], data[2]);
                    break;
                case MAGNETIC_FIELD_ID:
                    this.MagneticField = new Vector3(data[0], data[1], data[2]);
                    break;
                case ORIENTATION_ID:
                    this.Orientation =  new Vector3(data[0], data[1], data[2]);
                    break;
                case PROXIMITY_ID:
                    this.Proximity = data[0];
                    break;
                case HUMIDITY_ID:
                    this.Humidity = data[0];
                    break;
                case AIR_PRESSURE_ID:
                    this.Pressure = data[0];
                    break;
                case GPS_ID:
                    this.GPSLocation = new Vector2(data[0], data[1]);
                    break;
                case GRAVITY_ID:
                    this.Gravity = new Vector3(data[0], data[1], data[2]);
                    break;
                default:
                    throw new ArgumentException(string.Format("A sensor with id: {0} is not supported", sensorEvent.SensorID));
            }

        }

        #region Touch

        List<Touch> touches;
        public List<Touch> Touches
        {
            get { return this.touches; }
        }

        public event Action<TouchEvent> Touch;
        private void OnTouch(TouchEvent touchEvent)
        {
            this.touches = touchEvent.Touches;

            if (this.Touch != null)
                this.Touch(touchEvent);

            this.touchEventManager.ProcessEvent(touchEvent);
        }

        public event Action Tap;
        internal void OnTap()
        {
            if (this.Tap != null)
                this.Tap();
        }

        
        public event Action LongTap;
        internal void OnLongTap()
        {
            if (this.LongTap != null)
                this.LongTap();
        }

        public event Action Down;
        internal void OnDown()
        {
            if (this.Down != null)
                this.Down();
        }

        public event Action Up;
        internal void OnUp()
        {
            if (this.Up != null)
                this.Up();
        }

        public event Action<int, int> Move;
        internal void OnMove(int dx, int dy)
        {
            if (this.Move != null)
                this.Move(dx, dy);
        }

        public event Action<int> MultiTap;
        internal void OnMultiTap(int count)
        {
            if (this.MultiTap != null)
                this.MultiTap(count);
        }

        public event Action<int, int, int> MulitiMove;
        internal void OnMultiMove(int count, int dx, int dy)
        {
            if (this.MulitiMove != null)
                this.MulitiMove(count, dx, dy);
        }

        public event Action<int> Pinch;
        internal void OnPinch(int pinch)
        {
            if (this.Pinch != null)
                this.Pinch(pinch);
        }

        #endregion
    }
}
