using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class Phone
    {
        private readonly IMessageDecoder messageDecoder;

        public Phone(IMessageDecoder messageDecoder)
        {
            this.messageDecoder = messageDecoder;
            this.messageDecoder.Phone = this;
        }

        public void ProcessMessage(byte[] data)
        {
            this.messageDecoder.DecodeMessage(data);
        }


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

        #region Phone Events 

        public event Action Tapped;
        internal void OnTap()
        {
            if (this.Tapped != null)
                this.Tapped();
        }

        public event Action LongTapped;
        internal void OnLongTap()
        {
            if (this.LongTapped != null)
                this.LongTapped();
        }

        public event Action<int,int> TouchMoved;
        internal void OnTouchMove(int dx, int dy)
        {
            if (this.TouchMoved != null)
                this.TouchMoved(dx, dy);
        }

        public event Action VolumeUp;
        internal void OnVolumeUp()
        {
            if (this.VolumeUp != null)
                this.VolumeUp();
        }

        public event Action VolumeDown;
        internal void OnVolumeDown()
        {
            if (this.VolumeDown != null)
                this.VolumeDown();
        }

        public event Action<String> NetworkedText;
        internal void OnNetworkedText(string text)
        {
            if (this.NetworkedText != null)
                this.NetworkedText(text);
        }

        public event Action<int> VerticalScroll;
        internal void OnVerticalScroll(int delta)
        {
            if (this.VerticalScroll != null)
                this.VerticalScroll(delta);
        }


        public event Action<int> Horizontalscroll;
        internal void OnHorizontalScroll(int delta)
        {
            if (this.Horizontalscroll != null)
                this.Horizontalscroll(delta);
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
    }
}
