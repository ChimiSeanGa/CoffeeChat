import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class DataServer implements Runnable {
	Socket client;
	static ArrayList<Socket> clientList = new ArrayList<Socket>();

	public DataServer(Socket clientSocket) {
		client = clientSocket;
	}

	public void run() {
		if (client == null)
			System.exit(1);

		try {
			BufferedReader in = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
			BufferedReader stdIn =
				new BufferedReader(
					new InputStreamReader(System.in));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				synchronized(clientList) {
					for (Socket c : clientList) {
						PrintWriter out = 
							new PrintWriter(c.getOutputStream(), true);
						out.println(inputLine);
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println("IOException caught");
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		ServerSocket serverSocket = 
				new ServerSocket(portNumber);

		while (true) {
			Socket clientSocket = serverSocket.accept();

			synchronized(clientList) {
				clientList.add(clientSocket);
			}

			(new Thread(new DataServer(clientSocket))).start();
		}
	}
}