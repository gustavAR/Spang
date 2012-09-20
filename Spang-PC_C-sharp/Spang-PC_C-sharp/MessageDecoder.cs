﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Spang_PC_C_sharp
{
    class MessageDecoder : IMessageDecoder
    {
        private IDictionary<byte, Action<BinaryReader>> handlers;
        private Phone phone;
        public Phone Phone
        {
            set { this.phone = value; }
        }

        public MessageDecoder()
        {
            this.handlers = new Dictionary<byte, Action<BinaryReader>>();
       
            this.handlers.Add(0, this.HandleTap);
            this.handlers.Add(1, this.HandleLongTap);
            this.handlers.Add(2, this.HandleTouchMove);
            this.handlers.Add(3, this.HandleAccelerometerUpdate);
            this.handlers.Add(4, this.HandleLuminanceUpdate);
            this.handlers.Add(5, this.HandleGyroscopeUpdate);
            this.handlers.Add(6, this.HandleMagneticFieldUpdate);
            this.handlers.Add(7, this.HandleVolumeUp);
            this.handlers.Add(8, this.HandleVolumeDown);
            this.handlers.Add(9, this.HandleProximityUpdate);
            this.handlers.Add(10, this.HandleNetworkedText);
            this.handlers.Add(11, this.HandleVerticalScroll);
            this.handlers.Add(12, this.HandleHorizontalScroll);
            this.handlers.Add(13, this.HandlePressureUpdate);
            this.handlers.Add(14, this.HandleOrientationUpdate);
            this.handlers.Add(15, this.HandleGPSUpdate);
        }

        public void DecodeMessage(byte[] message)
        {
            var memstream = new MemoryStream(message);
            var reader = new BinaryReader(memstream);
            while (memstream.Position < memstream.Length)
            {
                var b = reader.ReadByte();
                var handler = handlers[b];
                handler(reader);
            }
        }

        private void HandleTap(BinaryReader reader)
        {
            this.phone.OnTap();
        }

        private void HandleLongTap(BinaryReader reader)
        {
            this.phone.OnLongTap();
        }

        private void HandleTouchMove(BinaryReader reader)
        {
            this.phone.OnTouchMove(reader.ReadInt32(), reader.ReadInt32());
        }

        private void HandleVolumeUp(BinaryReader reader)
        {
            this.phone.OnVolumeUp();
        }

        private void HandleVolumeDown(BinaryReader reader)
        {
            this.phone.OnVolumeDown();
        }

        private void HandleVerticalScroll(BinaryReader reader)
        {
            this.phone.OnVerticalScroll(reader.ReadInt32());
        }

        private void HandleHorizontalScroll(BinaryReader reader)
        {
            this.phone.OnHorizontalScroll(reader.ReadInt32());
        }

        private void HandleAccelerometerUpdate(BinaryReader reader)
        {
            this.phone.Accelerometer = ReadVector3(reader);
        }

        private void HandleLuminanceUpdate(BinaryReader reader)
        {
            this.phone.Luminance = reader.ReadSingle();
        }

        private void HandleGyroscopeUpdate(BinaryReader reader)
        {
            this.phone.Gyroscope = ReadVector3(reader);
        }

        private void HandleMagneticFieldUpdate(BinaryReader reader)
        {
            this.phone.MagneticField = ReadVector3(reader);
        }

        private void HandleProximityUpdate(BinaryReader reader)
        {
            this.phone.Proximity = reader.ReadSingle();
        }

        private void HandleNetworkedText(BinaryReader reader)
        {
            int length = reader.ReadInt16();
            byte[] textValue = reader.ReadBytes(length);
            String text = Encoding.UTF8.GetString(textValue);

            this.phone.OnNetworkedText(text);
        }

        private void HandlePressureUpdate(BinaryReader reader)
        {
            this.phone.Pressure = reader.ReadSingle();
        }

        private void HandleOrientationUpdate(BinaryReader reader)
        {
            this.phone.Orientation = ReadVector3(reader);
        }

        private void HandleGPSUpdate(BinaryReader reader)
        {
            this.phone.GPSLocation = new Vector2(reader.ReadSingle(), reader.ReadSingle());
        }

        private Vector3 ReadVector3(BinaryReader reader)
        {
            return new Vector3 { X = reader.ReadSingle(), Y = reader.ReadSingle(), Z = reader.ReadSingle() };
        }
    }
}
