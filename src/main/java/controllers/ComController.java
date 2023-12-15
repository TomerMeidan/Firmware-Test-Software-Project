package controllers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

import model.CapturePacket;
import util.Parser;

public class ComController extends Communicator {

	protected PortalViewInterface view;
	private Timer timer;
	private boolean gotComConnectFromServer = false;

	public ComController(String ip, int port) {
		super(ip, port);
	}

	public void setPortal(ClientPortalView view) {
		this.view = view;
	}

	public void start() {
		view.init();

		// log
		System.out.println("ClientController: PortalView initialized");

		tryToConnect();
	}

	public void stop() {
		try {
			closeConnectionToServer();
		} catch (IOException e) {
			// log
			System.out.println("ClientController: IOException in stop()");
		}
	}

	private void tryToConnect() {

		try {
			openConnectionToServer();
		} catch (IOException e) {
			System.out.println(e.toString());

			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					// log
					System.out.println("trying to connect...");

					tryToConnect();
				};
			};

			timer.schedule(task, 5000);

		}
	}

	@Override
	public void connectionEstablished() {
		// log
		System.out.println("ClientController: connection established");

		if (timer != null) {
			timer.cancel();
			System.out.println("ClientController: timer closed");
		}
	}

	@Override
	public void handleMessageFromServer(byte[] msg) {

		if (msg == null) {
			// log
			System.out.println("ClientController: Received null from server");
			return;
		}
		System.out.println("ClientController: Received message from server");

		if (msg.length == 1) {
			handleStatusUpdate(msg);
		} else {
			CapturePacket packetFromServer = CapturePacket.deserialize(msg);

			// log
			System.out.println("ClientController: message : " + packetFromServer.toString());

			switch ((short) packetFromServer.getOpcode()) {
			case (short) 0x0702:
				// log
				System.out.println("ClientController: messageType: Controller communication connect message");
				// send com connect back to server
				gotComConnectFromServer = true;
				handleUserAction(msg);
				break;
			default:
				// log
				System.out.println("ClientController: messageType: undefined");
				break;
			}
		}

	}

	private void handleStatusUpdate(byte[] msg) {
		String statusMessage;

		switch ((byte) msg[0]) {

		case (byte) 0x16:
			statusMessage = "Nack 0x16 Pedestal Unavailable";
			break;
		case (byte) 0x76:
			statusMessage = "Nack 0x76 Video Tracker Unavailable";

			break;
		case (byte) 0xA6:
			statusMessage = "Nack 0xA6 Invalid Command";

			break;

		case (byte) 0xB6:
			statusMessage = "Nack 0xB6 Invalid Motor Checksum";

			break;

		case (byte) 0xE6:
			statusMessage = "Nack 0xE6 Execution Error";

			break;

		case (byte) 0xF6:
			statusMessage = "Nack 0xF6 Wrong Checksum";
			break;

		case (byte) 0x06:

			// enter here is controller sent ack for the com connect packet
			if (gotComConnectFromServer) {
				gotComConnectFromServer = false;

				JSONObject json = new JSONObject();
				json.put("command", "online");
				view.handleMsg(json);
				
				return;
			} else
				statusMessage = "Ack 0x06 Acknowledge";
			break;

		default:
			statusMessage = "An unknown response";
			break;
		}

		JSONObject json = new JSONObject();
		json.put("command", "server response");
		json.put("message", statusMessage);

		view.handleMsg(json);
	}

	public void handleUserAction(byte[] msg) {
		try {
			sendToServer(msg);
		} catch (IOException e) {
			System.out.println("ClientController: IOException in handleUserAction: ");
		}
	}

	@Override
	protected void connectionClosed() {
		// log
		System.out.println("ClientController: connection closed");

		closeAll();

	}

	@Override
	protected void connectionException(Exception exception) {
		// log
		System.out.println("ClientController: connection exception");
		System.out.println(exception);
		/*
		 * if connection falls - back to login window and try to reconnect. a delay is
		 * needed because of the sequence in AbstractClient.run() connectionException()
		 * is called before clientReader = null;
		 */
		timer = new Timer();
		TimerTask task = new TimerTask() {

			public void run() {
				// log
				System.out.println("trying to connect...");

				start();
			};
		};

		timer.schedule(task, 1000);
	}

	public void closeAll() {

		if (timer != null) {
			timer.cancel();
			System.out.println("ClientController: timer closed");
		}
	}

}
