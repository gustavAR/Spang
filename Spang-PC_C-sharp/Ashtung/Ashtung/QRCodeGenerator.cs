using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Drawing.Imaging;
using com.google.zxing.qrcode;
using com.google.zxing.common;
using com.google.zxing;

namespace Ashtung
{
    class QRCodeGenerator
    {
        /// <summary>
        /// Opens up a generated image containing a QR-code within which
        /// str is encoded.
        /// 
        /// The image can be found inside the bin folder.
        /// </summary>
        /// <param name="str">Content of the generated QR-code</param>
        /// <param name="width">Width of the image</param>
        /// <param name="height">Height of the image</param>
        /// <param name="format">Format of the image file</param>
        public static void ShowQRCode(string str, int width, int height, ImageFormat format)
        {
            Bitmap bmp = GenerateQRC(str, width, height);
            string address = @"..\AdrportQR." + format.ToString();
            bmp.Save(address, format);
            System.Diagnostics.Process.Start(address);
        }

        /// <summary>
        /// Generates a bitmap containing a QR-code
        /// The bitmap will be padded with some space in the margins.
        /// </summary>
        /// <param name="str">The string which we encode into the QR-code</param>
        /// <param name="width">Height of the resulting bitmap</param>
        /// <param name="height">Width of the resulting bitmap</param>
        /// <returns>A bitmap containing a QR-code containing the input string with padding.</returns>
        public static Bitmap GenerateQRC(string str, int width, int height)
        {

            QRCodeWriter qrCode = new QRCodeWriter();
            ByteMatrix byteIMG = qrCode.encode(str, BarcodeFormat.QR_CODE, width, height);

            sbyte[][] img = byteIMG.Array;
            Bitmap bmp = new Bitmap(width, height);
            Graphics g = Graphics.FromImage(bmp);

            fillImage(img, g);
            return bmp;
        }

        /// <summary>
        /// Fills the bitmap contained within g with the contents of img.
        /// </summary>
        private static void fillImage(sbyte[][] img, Graphics g)
        {
            g.Clear(Color.White);
            for (int i = 0; i <= img.Length - 1; i++)
            {
                for (int j = 0; j <= img[i].Length - 1; j++)
                {
                    if (img[j][i] == 0)
                    {
                        g.FillRectangle(Brushes.Black, i, j, 1, 1);
                    }
                    else
                    {
                        g.FillRectangle(Brushes.White, i, j, 1, 1);
                    }
                }
            }
        }
    }
}
