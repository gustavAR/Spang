using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Android
{
    public class TouchEvent
    {
        public List<Touch> Touches { get; set; }

        public TouchEvent(params Touch[] touches)
        {
            this.Touches = new List<Touch>();
            foreach (var item in touches)
            {
                this.Touches.Add(item);
            }
        }
    }
}
