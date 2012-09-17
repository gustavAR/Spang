package PC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements Runnable {
	private final static int PORT = 10248;
	
	private PrintWriter writer;
	private BufferedReader reader;
	private Socket socket;
	
	
	public static void main(String ... args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);		
		
		while(true) {
			Socket socket = serverSocket.accept();	
			
			Server server = new Server();
			server.socket = socket;
			server.writer = new PrintWriter(socket.getOutputStream());
			server.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));			
			new Thread(server).start();
		}
	}


	public void run() {
		char c;
		try {
			while((c = (char)reader.read()) != -1) {
				System.out.print(c);
			}
		} catch (IOException e) { }	
	}
}
