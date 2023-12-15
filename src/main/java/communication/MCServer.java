package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import model.CapturePacket;

public class MCServer {

	private static int port = 4949;

	public static void main(String[] args) throws ClassNotFoundException {

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket clientSocket = serverSocket.accept();

				ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());

				CapturePacket comConnectPacket = new CapturePacket();
				comConnectPacket.setOpcodeHigh((byte) 0x07);
				comConnectPacket.setOpcodeLow((byte) 0x02);
				comConnectPacket.setOpcode((short) 0x0702);

				comConnectPacket.setLength((byte) 0x04);
				objectOutput.writeObject(comConnectPacket.getPacketBytes());
				byte[] receivedObject = (byte[]) objectInput.readObject();

				if (comConnectPacket.getOpcodeHigh() == receivedObject[5])
					if (comConnectPacket.getOpcodeLow() == receivedObject[6])
						objectOutput.writeObject((byte) 0x06);
				System.out.println("Controller sent COM_CONNECT to user");
				System.out.println("Connection established with a client.");

				// Create a new thread to handle the client connection
				Thread clientHandlerThread = new Thread(new ClientHandler(clientSocket));
				clientHandlerThread.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ClientHandler implements Runnable {
		private Socket clientSocket;

		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			try {
				// Set up object streams
				ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());

				// Read an object from the client
				Object receivedObject = objectInput.readObject();
				System.out.println("Received object from client: " + receivedObject);

				// Process the received object (you can define your logic here)

				// Send a response object back to the client
				Object responseObject = "Server response";
				objectOutput.writeObject(responseObject);
				objectOutput.flush();

				// Close the connections
				objectInput.close();
				objectOutput.close();
				clientSocket.close();

			} catch (SocketException e) {
				System.out.println("Client disconnected from server");
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
