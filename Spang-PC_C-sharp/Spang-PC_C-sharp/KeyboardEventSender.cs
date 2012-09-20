using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Spang_PC_C_sharp
{
    class KeyboardEventSender
    {

        public static void SendKey(char key)
        {
            SendKeys.SendWait("{" + key + "}");
        }
    }
}
