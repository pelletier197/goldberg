package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import views.screenController.LoadingController;
import views.screenController.ScreenController;
import views.screenController.ScreenController.Animations;
import views.screenController.Screens;

public class Main extends Application {

	/**
	 * The screen controller of the application
	 */
	private ScreenController mainPane;

	/**
	 * The scene of the application.
	 */
	private Scene scene;

	/**
	 * The stage of the application
	 */
	private Stage stage;

	/**
	 * Launches the game
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch();
	}

	/**
	 * Launches the game and start it at the MAIN_MENU view. Also loads the
	 * styles.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		this.stage = primaryStage;

		stage.setMaximized(true);

		this.mainPane = new ScreenController();
		this.scene = new Scene(mainPane);

		scene.getStylesheets().add(getClass().getResource("/styles/DarkThemeModena.css").toExternalForm());

		mainPane.setScreen(Screens.MAIN_MENU, Animations.FADE_IN);

		// Gives the loader view to the screenController;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaceViews/loadingView.fxml"));
		Node loadingScreen = loader.load();
		LoadingController loadControl = loader.getController();
		mainPane.setLoadingScreen(loadingScreen, loadControl);

		primaryStage.getIcons().add(new Image(getClass().getResource("/images/coinSmile.gif").toExternalForm()));
		primaryStage.setTitle("The GoldBerg Game ");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
