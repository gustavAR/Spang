using System;
namespace Spang.Core.Network
{
    public interface IConnectionListener
    {
        int ConnectTimeout { get; set; }
        IConnection ReciveConnection(int port);
    }
}
