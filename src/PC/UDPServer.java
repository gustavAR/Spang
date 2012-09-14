import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class UDPServer {
	
	private static final int PORT = 1337;
	private DatagramSocket socket;
	
	
	public static void main(String ... args) throws Exception {
		UDPServer server = new UDPServer();
		server.socket = new DatagramSocket(PORT);
		
		
		while(true) {
			byte[] recievDataBuffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(recievDataBuffer, 1024);
			server.socket.receive(packet);		
			String string = new String(packet.getData());
			
			System.out.println(string);
		}
	}
}
