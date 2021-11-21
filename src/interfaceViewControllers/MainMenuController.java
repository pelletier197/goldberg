package interfaceViewControllers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

import game.GameSong;
import game.Level;
import game.Settings;
import game.SoundMaker;
import gameObservableViews.ObservableObjectFactory;
import gameObservableViews.ObservableWrapper;
import gameObservables.Coin;
import gameObservables.Observable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import observables.AbstractComplexObservable;
import observables.DynamicWorld;
import observables.DynamicWorld.Bounds;
import observables.ScaleManager;
import utils.BufferingUtils;
import views.screenController.ControlledScreen;
import views.screenController.ScreenController;
import views.screenController.ScreenController.Animations;
import views.screenController.Screens;

public class MainMenuController implements ControlledScreen, Initializable {

	@FXML
	private BorderPane background;
	@FXML
	private Pane CoinCollisionPane;

	/**
	 * The dynamic world that displays the coin in the world .
	 */
	private DynamicWorld world;

	/**
	 * The screen controller
	 */
	private ScreenController controller;

	/**
	 * Called by the view when the user pressed the "create" button. Will load
	 * the {@link createInfoController}'s view, in order the user starts to
	 * create it's level.
	 * 
	 * @param event
	 */
	@FXML
	private void createClicked(ActionEvent event) {
		controller.setScreen(Screens.CREATE_INFO, Animations.TRANSLATE_LEFT_TO_CENTER);
	}

	/**
	 * Called by the view when the button "created levels" is clicked. Will
	 * display the {@link LevelViewController}'s view, after having loaded the
	 * levels in a closed thread that won't be interrupted.
	 * 
	 * @param event
	 */
	@FXML
	private void createdLevelsClicked(ActionEvent event) {

		// As the operation might be extremely long, we show the loader.
		controller.showLoader();
		new Thread(new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				// Get all the levels contained in the
				final List<Level> personnalLevels = BufferingUtils.readAllLevels(new File(Settings.PATH_TO_PERSONNAL));

				// Hides the loading view
				controller.hideLoader();

				// Switch of screen and show the CREATE_LEVEL view
				Platform.runLater(() -> {
					controller.setScreen(Screens.CREATED_LEVELS, Animations.TRANSLATE_LEFT_TO_CENTER);
					LevelViewController levelController = (LevelViewController) controller
							.getController(Screens.CREATED_LEVELS);
					levelController.setLevels(personnalLevels, true);
					levelController.setTitle("Niveaux Créés");
				});
				return null;
			}
		}).start();

	}

	/**
	 * Called when the user clicked exit.
	 * 
	 * @param event
	 */
	@FXML
	private void exitClicked(ActionEvent event) {
		Platform.exit();
	}

	/**
	 * Called by the view when the "play" button is clicked. Will load the
	 * campain levels, and show them into the CREATED_LEVELS view.
	 * 
	 * @param event
	 */
	@FXML
	private void playClicked(ActionEvent event) {
		// As the operation might be extremely long, we show the loader.
		controller.showLoader();

		new Thread(new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				// Get all the levels contained in the default folder
				final List<Level> personnalLevels = BufferingUtils.readAllLevels(new File(Settings.PATH_TO_DEFAULT));

				// Hides the loading view
				controller.hideLoader();

				// Switch of screen and show the CREATE_LEVEL view
				Platform.runLater(() -> {

					controller.setScreen(Screens.CREATED_LEVELS, Animations.TRANSLATE_LEFT_TO_CENTER);
					LevelViewController vController = (LevelViewController) controller
							.getController(Screens.CREATED_LEVELS);
					vController.setLevels(personnalLevels, false);
					vController.setTitle("Niveaux");
				});
				return null;
			}
		}).start();

	}

	/**
	 * Called when the logo is clicked. Gives random directions to the coin
	 * displayed in {@link #CoinCollisionPane}.
	 * 
	 * @param event
	 */
	@FXML
	private void easterEgg(MouseEvent event) {
		randomDirections();
	}

	/**
	 * Gives random angular and linear velocity to the coin contained in
	 * {@link #world}.
	 */
	private void randomDirections() {

		final List<AbstractComplexObservable> objects = world.getObservables();

		for (AbstractComplexObservable object : objects) {

			if (object instanceof Coin) {

				object.applyForce(
						new Vector2(Math.random() * 4 - 4 * Math.random(), Math.random() * 4 - 4 * Math.random()));

				object.getBodies().get(0).setAngularVelocity(Math.random() * 5 - Math.random() * 5);

			}
		}
	}

	/**
	 * Create the world. Bind the world's width and height to the pane's width
	 * and height, to ensure coins collide in the full space available. Will
	 * generate the coins' wrappers, and instantiate them in the {@link #world}
	 */
	private void createWorld() {
		/*
		 * Creates a DoubleBinding that converts the pane's height in meters for
		 * the world to work perfectly.
		 */
		DoubleBinding height = Bindings.createDoubleBinding(() -> {
			return ScaleManager.pixelToMeters(CoinCollisionPane.getHeight());
		} , CoinCollisionPane.heightProperty());
		/*
		 * Creates a DoubleBinding that converts the pane's width in meters for
		 * the world to work perfectly.
		 */
		DoubleBinding width = Bindings.createDoubleBinding(() -> {
			return ScaleManager.pixelToMeters(CoinCollisionPane.getWidth());
		} , CoinCollisionPane.widthProperty());

		// Initializing the world and its objects
		world = new DynamicWorld(height, width, 0);
		world.setAllBound(Bounds.values());
		world.setDynamic(true);

		// Generate the coins.
		for (int i = 1; i < 11; i++) {
			ObservableWrapper wrapper = new ObservableObjectFactory().getWrapperInstance(Observable.COIN,
					(i % 3 + 1) / 3D, 0, 0);

			Coin coin = (Coin) wrapper.observable;
			coin.translate(3, 3);

			// Mass proportional to radius
			coin.getBodies().get(0).setMass(new Mass(new Vector2(0, 0), 0.002 * (i % 3 + 1), 0.005));

			world.addAllComplexObjects(coin);
			wrapper.view.setPickOnBounds(false);
			CoinCollisionPane.getChildren().add(wrapper.view);
		}

		// Gives random directions to coins
		randomDirections();

		// Gives the pane a scale to put the (0,0) point of the bottom left
		// corner of the screen.
		Scale scale = new Scale();
		scale.setX(1);
		scale.setY(-1);
		CoinCollisionPane.getTransforms().add(scale);
		CoinCollisionPane.translateYProperty().bind(background.heightProperty());

		// Gives it a star background
		CoinCollisionPane.setBackground(new Background(new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/etoile.jpg")), BackgroundRepeat.REPEAT,
				BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
	}

	/**
	 * Called to initialize the menu.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createWorld();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScreenController(ScreenController SC) {
		this.controller = SC;

	}

	/**
	 * Starts the {@link #world} to update and detect collisions. Also give the
	 * coins random direction and starts the menu's theme song.
	 */
	@Override
	public void displayedToScreen() {
		world.start();
		randomDirections();
		SoundMaker.playSong(GameSong.MAIN_MENU);
	}

	/**
	 * Stops the world to update collisions.
	 */
	@Override
	public void removedFromScreen() {
		world.pause();
	}
}
