using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Android
{
    public class HardwareButtonEvent
    {
        public const int VOLUME_UP = 0x01;
	    public const int VOLUME_DOWN = 0x02;
	    public const int SEARCH = 0x04;
	
	    public readonly int ID;
	
	    public HardwareButtonEvent(int id) {
		    this.ID = id;
	    }
    }
}
