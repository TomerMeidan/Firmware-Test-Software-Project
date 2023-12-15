package communication;

import controllers.ComController;
import controllers.ClientPortalView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MCClient extends Application {

	private static int PORT = 4949;
	private static String ADDRESS = "localhost"; // Replace with the actual server address
	private static ComController com;
	private ClientPortalView clientPortalView;

	public static void main(String[] args) {

		launch(args);
		System.out.println("MCClient: javaFX window application stopped");
		// com.stop();

		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		com = new ComController(ADDRESS, PORT);
		clientPortalView = new ClientPortalView(primaryStage, com);
		com.setPortal(clientPortalView);
		// log
		System.out.println("MCClient: ClientController initialized");

		com.start();
	}

}
