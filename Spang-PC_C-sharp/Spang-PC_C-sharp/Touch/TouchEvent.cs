using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class TouchEvent
    {
        public List<Vector3> Pointers { get; set; }

        public static TouchEvent Empty { get; set; }
    }
}
