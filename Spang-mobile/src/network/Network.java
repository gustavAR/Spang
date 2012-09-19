package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Network {

	public static void ListenToBroadcasts(final int port, final INetworkListener listener){
		new Thread(new Runnable() {
			
			public void run() {
				try {
					DatagramSocket udpSocket = new DatagramSocket(port);
					DatagramPacket packet = new DatagramPacket(new byte[100], 100);
					udpSocket.receive(packet);
					
					byte[] copy = new byte[packet.getLength()];
					for (int i = 0; i < copy.length; i++) {
						copy[i] = packet.getData()[i];
					}
					
					listener.listen(copy);
				} catch (Exception e) {
					throw new NetworkException(e);
				}
				
			}
		});
	}
}
