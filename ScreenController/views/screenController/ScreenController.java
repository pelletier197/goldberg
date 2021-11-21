package views.screenController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * {@link #ScreenController()} is basically a class that provides all tools for
 * javaFx transition beetweenScreens. This class extends BorderPane, and should
 * normally be used as the main root of an application.
 * 
 * To use it, every controller that wants to change the screen displayed by the
 * application should implement the {@link #ControlledScreen} Interface from the
 * same package.
 * 
 * Once it's done, when the controller will be loaded by FXML, this class will
 * call ControlledScreen.setController(ScreenController). Using the object sent,
 * the controller can call {@link #setScreen(Screens)} method, and choose the
 * screen displayed to screen based on the {@link #Screens} enumeration of
 * Screens.
 * 
 * The new view will usually be loaded if it's not done yet, and will be charged
 * in the {@link #getCenter()} of the ScreenController
 * 
 * Once the view is loaded, the prefWidth of this object will be set to the new
 * view's prefWidth, if this one is bigger than the actual prefWidth.
 * 
 * @author sunny
 *
 */
public class ScreenController extends StackPane {

	/**
	 * The hashmap of screens in memory. The key is an enum of screens
	 */
	private HashMap<Screens, ScreenControllerWrapper> screenMap;

	private Node loader;
	private LoadingController loaderControl;

	/**
	 * The stack of screens that has been loaded in the past.
	 */
	private Stack<Screens> lastScreens;

	/**
	 * The actual screen that is displayed.
	 */
	private Screens actualScreen;

	/**
	 * Available animations for transitions
	 *
	 */
	public enum Animations {
		FADE_IN, TRANSLATE_TOP_TO_CENTER, TRANSLATE_BOTTOM_TO_CENTER, TRANSLATE_RIGHT_TO_CENTER, TRANSLATE_LEFT_TO_CENTER
	}

	/**
	 * A ScreenController is used to control the screen that are being displayed
	 * by an application. This class extends borderPane, and the center is
	 * usually where the screens sent to setScreen(Screens) method will be
	 * displayed.
	 * 
	 * The controller of all those views should be implementing the
	 * {@link ControlledScreen} interface. This interface will ensure that the
	 * application is entirely, but also that the controller will be able to
	 * change the screen that will be displayed by themselves, using the
	 * ScreenController - this sent by the method
	 * setScreenController(ScreenController).
	 */
	public ScreenController() {
		super();
		screenMap = new HashMap<>();
		lastScreens = new Stack<>();
	}

	/**
	 * Load the screen from the FXML file that is associated to the enum
	 * Screens.
	 * 
	 * @param screenToLoad
	 *            The screen that must be loaded.
	 */
	public void loadScreen(Screens screenToLoad) {
		// The fxmlloader
		FXMLLoader loader = new FXMLLoader(screenToLoad.getFxmlUrl());

		try {
			// Loads the node and controller
			Node node = loader.load();
			Object controller = loader.getController();

			if (!(controller instanceof ControlledScreen)) {
				throw new ClassCastException("Controller not instance of controlledScreen");
			}
			// Put the screen to the map
			screenMap.put(screenToLoad, new ScreenControllerWrapper(node, (ControlledScreen) controller));

			// Set the screenController of the Node's controller
			((ControlledScreen) controller).setScreenController(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Change the screen that is being displayed to the interface for the one
	 * sent in parameter. If the screen has not been loaded yet, it will be
	 * after this method has been called.
	 * 
	 * The screen will take place on a fadeIn effect, and a fadeOut effect from
	 * the old screen if there is one.
	 * 
	 * Moreover, it will be possible to make the previous screen back to the
	 * front view by calling setPreviousScreen() method from the same class.
	 * 
	 * @param screenToSet
	 *            The screen that will be set to the front view.
	 */
	public void setScreen(Screens screenToSet, Animations anim) {

		// If the screen is not loaded yet, we load it
		if (screenMap.get(screenToSet) == null) {
			loadScreen(screenToSet);
		}
		// get the screenControllerWrapper
		final ScreenControllerWrapper screen = screenMap.get(screenToSet);

		// Stores the old view to the cache to save memory and processor, as it
		// is not displayed anymore
		try {
			Node actualView = screenMap.get(actualScreen).view;
			actualView.setCacheHint(CacheHint.DEFAULT);
			actualView.setCache(true);
		} catch (NullPointerException e) {
			// do nothing
		}
		// restore visibility
		screen.view.setVisible(true);
		screen.view.setOpacity(1);
		screen.view.setTranslateX(0);
		screen.view.setTranslateY(0);

		// Makes it uncachable to make it performing
		screen.view.setCache(false);

		// Advertise the controller that the view will soon be displayed to
		// screen, so it can update it's view
		screen.controller.displayedToScreen();

		if (actualScreen != null) {
			// Advertise the actual screen that it will be removed from the
			// screen.
			this.screenMap.get(actualScreen).controller.removedFromScreen();
		}

		// If there is no node in the center, fadeIn only
		if (getChildren().isEmpty() || anim == Animations.FADE_IN) {
			fadeInAnimation(screen);
		} else if (Arrays.asList(Animations.values()).contains(anim)) {
			createTranslateAnimation(anim, screen);
		}

		/*
		 * The actual screen is pushed into the stack of screens Loaded and is
		 * changed to the screen that has been loaded.
		 */
		if (actualScreen != null) {
			lastScreens.push(actualScreen);

		}
		actualScreen = screenToSet;
		if ((((Region) screen.view).getPrefWidth()) > getWidth())
			this.setPrefWidth((((Region) screen.view).getPrefWidth()));
	}

	private void createTranslateAnimation(Animations anim, ScreenControllerWrapper screen) {
		Region center = (Region) getChildren().remove(0);
		Region newCenter = (Region) screen.view;

		StackPane animBox = new StackPane(center, newCenter);
		animBox.setAlignment(Pos.CENTER);

		animBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		try {
			this.getChildren().set(0, animBox);
		} catch (IndexOutOfBoundsException e) {
			this.getChildren().add(animBox);
		}

		Timeline translateAnim = null;

		// Go get the desired translate animation depending on the anim
		// parameter
		if (anim == Animations.TRANSLATE_BOTTOM_TO_CENTER) {
			translateAnim = bottomToCenterAnimation(center, newCenter);
		} else if (anim == Animations.TRANSLATE_LEFT_TO_CENTER) {
			translateAnim = leftToCenterAnimation(center, newCenter);
		} else if (anim == Animations.TRANSLATE_RIGHT_TO_CENTER) {
			translateAnim = rightToCenterAnimation(center, newCenter);
		} else if (anim == Animations.TRANSLATE_TOP_TO_CENTER) {
			translateAnim = topToCenterAnimation(center, newCenter);
		}

		translateAnim.setOnFinished((event) -> {

			// Free the actual view from this content.
			animBox.getChildren().clear();

			this.getChildren().set(0, newCenter);

		});
		translateAnim.play();

	}

	private Timeline topToCenterAnimation(Region center, Region newCenter) {

		newCenter.setTranslateY(-2 * newCenter.getHeight());
		center.setTranslateY(0);

		return new Timeline(
				new KeyFrame(Duration.millis(500), new KeyValue(center.translateYProperty(), 2 * center.getHeight())),
				new KeyFrame(Duration.millis(500), new KeyValue(newCenter.translateYProperty(), 0)));

	}

	private Timeline rightToCenterAnimation(Region center, Region newCenter) {

		newCenter.setTranslateX(-2 * newCenter.getWidth());
		center.setTranslateX(0);

		return new Timeline(
				new KeyFrame(Duration.millis(500), new KeyValue(center.translateXProperty(), 2 * center.getWidth())),
				new KeyFrame(Duration.millis(500), new KeyValue(newCenter.translateXProperty(), 0)));

	}

	private Timeline leftToCenterAnimation(Region center, Region newCenter) {

		newCenter.setTranslateX(2 * newCenter.getWidth());
		center.setTranslateX(0);

		return new Timeline(
				new KeyFrame(Duration.millis(500), new KeyValue(center.translateXProperty(), -2 * center.getWidth())),
				new KeyFrame(Duration.millis(500), new KeyValue(newCenter.translateXProperty(), 0)));

	}

	private Timeline bottomToCenterAnimation(Region center, Region newCenter) {

		newCenter.setTranslateY(2 * newCenter.getHeight());
		center.setTranslateY(0);

		return new Timeline(
				new KeyFrame(Duration.millis(500), new KeyValue(center.translateYProperty(), -2 * center.getHeight())),
				new KeyFrame(Duration.millis(500), new KeyValue(newCenter.translateYProperty(), 0)));

	}

	private void fadeInAnimation(ScreenControllerWrapper screen) {
		if (getChildren().isEmpty()) {
			try {
				this.getChildren().set(0, screen.view);
			} catch (IndexOutOfBoundsException e) {
				this.getChildren().add(screen.view);
			}

			Timeline fadeIn = new Timeline(
					// @formatter : off
					new KeyFrame(Duration.ZERO, new KeyValue(this.getChildren().get(0).opacityProperty(), 0)),
					new KeyFrame(Duration.millis(800), new KeyValue(this.getChildren().get(0).opacityProperty(), 1)));
			// @formatter : on
			fadeIn.play();

		} else {
			// @formatter:off
			/*
			 * Fades out the old screen and fades in the new one
			 */
			Timeline fadeOut = new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(this.getChildren().get(0).opacityProperty(), 1)),
					new KeyFrame(Duration.millis(500), new KeyValue(this.getChildren().get(0).opacityProperty(), 0)));

			fadeOut.setOnFinished((event) -> {
				screen.view.setOpacity(0);
				getChildren().set(0, screen.view);
				/*
				 * Fades in the new screen
				 */
				Timeline fadeIn = new Timeline(
						new KeyFrame(Duration.ZERO, new KeyValue(this.getChildren().get(0).opacityProperty(), 0)),
						new KeyFrame(Duration.millis(500),
								new KeyValue(this.getChildren().get(0).opacityProperty(), 1)));
				fadeIn.play();
			});
			fadeOut.play();
			// @formatter:on
		}
	}

	public void setScreen(Screens screen) {
		if (getChildren().isEmpty()) {
			setScreen(screen, Animations.FADE_IN);
		} else {
			final Animations[] animations = Animations.values();
			setScreen(screen, animations[(int) (Math.random() * animations.length)]);
		}
	}

	/**
	 * Loads the screen that has previously been loaded. If there is no previous
	 * screen, no change is done.
	 */
	public void setPreviousScreen() {

		if (hasPreviousScreen()) {
			// Pop the actual screen, because not desired to be stacked.
			setScreen(lastScreens.pop());
		}

	}

	/**
	 * Allows to know if there is a previous screen in the stack of screens or
	 * not.
	 * 
	 * @return True if the stack has previous screen, false otherwise
	 */
	public boolean hasPreviousScreen() {
		return !lastScreens.isEmpty();
	}

	/**
	 * Used to define the laoding screen that is applied when
	 * 
	 * @param screen
	 * @param controller
	 */
	public void setLoadingScreen(Node screen, LoadingController controller) {
		this.loader = screen;
		this.loaderControl = controller;
	}

	/**
	 * Shows the application loading screen, and tells to its controller to
	 * start the animation if necessary. The loader will be displayed in the
	 * pane until the {@link #hideLoader()} method gets called by any of the
	 * children screens.
	 */
	public void showLoader() {
		if (loader == null) {
			throw new NullPointerException("No loading screen has been set");
		} else {
			if (!getChildren().contains(loader)) {
				getChildren().add(loader);
				Timeline fadeIn = new Timeline(
						// @formatter : off
						new KeyFrame(Duration.ZERO, new KeyValue(loader.opacityProperty(), 0)),
						new KeyFrame(Duration.millis(300), new KeyValue(loader.opacityProperty(), 1)));
				// @formatter : on
				fadeIn.play();
				this.loaderControl.startLoading();
			}
		}
	}

	/**
	 * Hides the application loading screen. This method has not effect if
	 * {@link #showLoader()} has not been called previously by one of the
	 * children screens.
	 */
	public void hideLoader() {
		if (loader == null) {
			throw new NullPointerException("No loading screen has been set");
		} else {
			if (getChildren().contains(loader)) {
				Timeline fadeout = new Timeline(
						// @formatter : off
						new KeyFrame(Duration.ZERO, new KeyValue(loader.opacityProperty(), 0)),
						new KeyFrame(Duration.millis(300), new KeyValue(loader.opacityProperty(), 1)));
				// @formatter : on
				fadeout.play();
				fadeout.setOnFinished((event) -> {
					this.getChildren().remove(loader);
				});

				this.loaderControl.stopLoading();

			}
		}
	}

	/**
	 * Allows to load the theme specified in parameter.
	 */
	public void setTheme(Themes theme) {
		this.getStylesheets().add(theme.toString());
	}

	/**
	 * @param screen
	 * @return Returns the screen's controller associated with the screen value.
	 */
	public Object getController(Screens screen) {
		return screenMap.get(screen).controller;
	}

	/**
	 * A simple wrapper for a view and its controller, used to keep them
	 * together
	 * 
	 * @author Sunny
	 */
	private class ScreenControllerWrapper {

		public final ControlledScreen controller;
		public final Node view;

		public ScreenControllerWrapper(Node view, ControlledScreen controller) {
			this.view = view;
			this.controller = controller;
		}

	}

}
