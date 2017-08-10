import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;

public class DataClient implements Runnable {
	Socket s;
	String username;
	JTextArea display;

	public DataClient(Socket socket, String user, JTextArea body) {
		s = socket;
		username = user;
		display = body;
	}

	public void run() {
		// Return with error value if no socket
		if (s == null) {
			System.exit(1);
		}

		try {
			// Create reader from socket
			BufferedReader in = 
				new BufferedReader(
					new InputStreamReader(s.getInputStream()));

			// Read in message and write it to text display
			String readStr;
			while ((readStr = in.readLine()) != null) {
				display.append(readStr + "\n");
			}
		}
		catch (IOException e) {
			System.out.println("IOException caught");
		}
	}

	public static void main(String[] args) throws IOException {
		// Get username and set IP and port accordingly
		final String user = JOptionPane.showInputDialog("Input username: ");
		String hostName = "localhost";
		int portNumber = 9090;

		// Create main window
		JFrame window = new JFrame("Coffee Chat");
		JPanel mainframe = new JPanel(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, 200, 200);

		// Create components add them to frame
		JTextArea body = new JTextArea();
		body.setEditable(false);
		JScrollPane scroll = new JScrollPane(body);
		mainframe.add(scroll, BorderLayout.CENTER);

		JPanel inputField = new JPanel(new BorderLayout());

		final JTextField msgInput = new JTextField();
		inputField.add(msgInput, BorderLayout.CENTER);

		JButton sendBtn = new JButton();
		sendBtn.setText("Send");
		inputField.add(sendBtn, BorderLayout.LINE_END);

		mainframe.add(inputField, BorderLayout.PAGE_END);

		// Add frame to window and make it visible on user's screen
		window.getContentPane().add(mainframe);
		window.pack();
		window.setSize(500, 300);
		window.setVisible(true);

		// Try to connect to server
		try {
			// Set up socket, writer, and reader
			Socket socket = new Socket(hostName, portNumber);
			final PrintWriter out = 
				new PrintWriter(socket.getOutputStream(), true);
			BufferedReader stdIn =
				new BufferedReader(
					new InputStreamReader(System.in));

			// Start new process for reading in messages
			(new Thread(new DataClient(socket, user, body))).start();

			// Add a listener to the button for sending messages
			sendBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					out.println(user + ": " + msgInput.getText());
					msgInput.setText("");
				}
			});

			// Main loop
			while (true) {
				;
			}
		}
		catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		}
		catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + 
				hostName);
			System.exit(1);
		}
	}
}