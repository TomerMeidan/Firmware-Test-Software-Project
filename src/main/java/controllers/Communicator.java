
package controllers;

import java.io.*;
import java.net.*;

/**
 * The Communicator encompasses all the methods essential for configuring the
 * client side of a client-micro-controller exchange. Once a client
 * establishes a connection with the micro-controller, the two entities can
 * seamlessly exchange Object instances, facilitating the transmission and
 * reception of data through Capture Systems Ltd. API calls.
 *
 */
public abstract class Communicator implements Runnable {

	// INSTANCE VARIABLES ***********************************************

	private String host;
	private int port;
	private Socket clientSocket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	/**
	 * The thread created to read data from the server.
	 */
	private Thread clientReader;

	/**
	 * Indicates if the thread is ready to stop. Needed so that the loop in the run
	 * method knows when to stop waiting for incoming messages.
	 */
	private boolean readyToStop = false;

	// CONSTRUCTORS *****************************************************

	public Communicator(String host, int port) {
		// Initialize variables
		this.host = host;
		this.port = port;
	}

	// INSTANCE METHODS *************************************************

	/**
	 * Opens the connection with the server. If the connection is already opened,
	 * this call has no effect.
	 */
	final public void openConnectionToServer() throws IOException {
		// Do not do anything if the connection is already open
		if (isConnected())
			return;

		// Create the sockets and the data streams
		try {
			clientSocket = new Socket(host, port);
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			input = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException ex)
		// All three of the above must be closed when there is a failure
		// to create any of them
		{
			try {
				closeAll();
			} catch (Exception exc) {
			}

			throw ex; // Rethrow the exception.
		}

		clientReader = new Thread(this); // Create the data reader thread
		readyToStop = false;
		clientReader.start(); // Start the thread
	}

	/**
	 * Sends an object to the server.
	 */
	final public void sendToServer(byte[] msg) throws IOException {
		if (clientSocket == null || output == null)
			throw new SocketException("socket does not exist");

		output.writeObject(msg);
	}

	/**
	 * Closes the connection to the server.
	 *
	 * @exception IOException if an I/O error occurs when closing.
	 */
	final public void closeConnectionToServer() throws IOException {
		// Prevent the thread from looping any more
		readyToStop = true;

		try {
			closeAll();
		} finally {
			// Call the hook method
			connectionClosed();
		}
	}

	// ACCESSING METHODS ------------------------------------------------

	/**
	 * @return true if the client is connnected.
	 */
	final public boolean isConnected() {
		return clientReader != null && clientReader.isAlive();
	}

	/**
	 * @return the port number.
	 */
	final public int getPort() {
		return port;
	}

	/**
	 * Sets the server port number for the next connection.
	 * 
	 * @param port the port number.
	 */
	final public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the host name.
	 */
	final public String getHost() {
		return host;
	}

	/**
	 * Sets the server host for the next connection.
	 */
	final public void setHost(String host) {
		this.host = host;
	}

	/**
	 * returns the client's description.
	 */
	final public InetAddress getInetAddress() {
		return clientSocket.getInetAddress();
	}

	// RUN METHOD -------------------------------------------------------

	/**
	 * Waits for messages from the server. When each arrives, a call is made to
	 * <code>handleMessageFromServer()</code>.
	 */
	final public void run() {
		connectionEstablished();

		// The message from the server
		Object msg;

		// Loop waiting for data

		try {
			while (!readyToStop) {
				msg = input.readObject();

				// Concrete subclasses do what they want with the
				// msg by implementing the following method
				byte[] byteArray;

				if (msg instanceof Byte) {
					byteArray = new byte[1];
					byteArray[0] = (byte) msg;
				} else
					byteArray = (byte[]) msg;

				handleMessageFromServer(byteArray);
			}
		} catch (Exception exception) {
			if (!readyToStop) {
				try {
					closeAll();
				} catch (Exception ex) {
				}

				connectionException(exception);
			}
		} finally {
			clientReader = null;
		}
	}

	// METHODS DESIGNED TO BE OVERRIDDEN BY CONCRETE SUBCLASSES ---------

	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing.
	 */
	protected void connectionClosed() {
	}

	/**
	 * Hook method called each time an exception is thrown by the client's thread
	 * that is waiting for messages from the server.
	 */
	protected void connectionException(Exception exception) {
	}

	/**
	 * Hook method called after a connection has been established.
	 */
	protected void connectionEstablished() {
	}

	/**
	 * Handles a message sent from the server to this client.
	 */
	protected abstract void handleMessageFromServer(byte[] msg);

	// METHODS TO BE USED FROM WITHIN THE FRAMEWORK ONLY ----------------

	/**
	 * Closes all aspects of the connection to the server.
	 */
	private void closeAll() throws IOException {
		try {
			// Close the socket
			if (clientSocket != null)
				clientSocket.close();

			// Close the output stream
			if (output != null)
				output.close();

			// Close the input stream
			if (input != null)
				input.close();
		} finally {
			output = null;
			input = null;
			clientSocket = null;
		}
	}
}
