package interfaceViewControllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.activation.UnsupportedDataTypeException;

import game.BorderType;
import game.GameSong;
import game.GoldbergGame;
import game.Inventory;
import game.InventoryItem;
import game.Level;
import game.Settings;
import game.SoundMaker;
import gameObservableControllers.ChildrenController;
import gameObservableControllers.ParentController;
import gameObservableViews.InventoryListCell;
import gameObservableViews.ObservableWrapper;
import gameObservables.Observable;
import interfaceViewControllers.EndGameController.State;
import interfaceViewControllers.headers.HeaderViewWrapper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.util.Callback;
import main.NumberField;
import observables.AbstractComplexObservable;
import observables.DynamicWorld;
import observables.ScaleManager;
import views.screenController.ControlledScreen;
import views.screenController.ScreenController;
import views.screenController.Screens;

public class MainGameController implements Initializable, ControlledScreen, ParentController {

	/**
	 * The image for the play arrow for {@link #play}
	 */
	private static final Image PLAY = new Image(
			MainGameController.class.getResource("/images/play_button.png").toExternalForm());

	/**
	 * The image for the pause bars for {@link #play}
	 */
	private static final Image PAUSE = new Image(
			MainGameController.class.getResource("/images/pause.png").toExternalForm());
	/**
	 * The first background of the view
	 */
	@FXML
	private BorderPane background;

	/**
	 * The bottom items holding the {@link #inventoryListView}
	 */
	@FXML
	private HBox bottomItems;

	/**
	 * The inventory displayed in the bottom of the screen
	 */
	@FXML
	private ListView<InventoryItem> inventoryListView;

	/**
	 * The right items, holding the tools for moving/rotating
	 */
	@FXML
	private VBox rightItems;

	/**
	 * The label telling the status
	 */
	@FXML
	private Label status;
	/**
	 * The gridpane holding translate X,Y Numberfields
	 */
	@FXML
	private GridPane positionGrid;

	/**
	 * Numberfield for position x
	 */
	private NumberField posX;
	/**
	 * Numberfield for position xy
	 */
	private NumberField posY;

	/**
	 * The label parameter, set to invisible when to header is displayed
	 */
	@FXML
	private Label param;

	/**
	 * The header wrapper, in which the headers are displayed
	 */
	@FXML
	private StackPane headerWrapper;

	/**
	 * The rotation slider for general settings
	 */
	@FXML
	private Slider rotationSlider;

	/**
	 * The rotation label binded to {@link #rotationSlider}
	 */
	@FXML
	private Label rotateLabel;

	/**
	 * The parent pane, used for rendering all panes to the center.
	 */
	@FXML
	private StackPane paneParent;

	/**
	 * The scrollpane for scalling scene's nodes
	 */
	@FXML
	private ScrollPane scrollPane;

	/**
	 * The center of {@link #background}, in which physic object's view is
	 * added.
	 */
	@FXML
	private Pane mainPane;

	/**
	 * The last background of the center, used to the display the planet's
	 * background
	 */
	@FXML
	private Pane planetBackground;

	/**
	 * Change listener for {@link #posX} and {@link #posY}
	 */
	private ChangeListener<Number> textInputListener;

	/**
	 * Change listener for {@link #rotationSlider}
	 */
	private ChangeListener<Number> rotationListener;

	/**
	 * The game that handles collisions and apply effects on static collisions
	 */
	private GoldbergGame game;

	

	/**
	 * The current clicked wrapper in the view
	 */
	private ObservableWrapper clickedWrapper;

	/**
	 * The effect for the {@link #clickedWrapper}
	 */
	private DropShadow selectedEffect;

	/**
	 * All the wrapper that this view generated and CAN modify WHEN needed
	 */
	private ObservableList<ObservableWrapper> children;

	/**
	 * Binding holding height and width of the world in pixels, for rendering
	 * purpose.
	 */
	private DoubleBinding heightInPixels, widthInPixels;

	/**
	 * Images for the borders, rendered inside the view.
	 */
	private ImageView top, right, bottom, left;

	/**
	 * The map of headers used in the view.
	 */
	private Map<Observable, HeaderViewWrapper> headers;

	/**
	 * Turned true when a drag gesture is detected
	 */
	private boolean isDragged;

	/**
	 * The actual level.
	 */
	private Level level;

	/**
	 * The background softner, used to make the center part of the game easier
	 * to see for the user.
	 */
	private javafx.scene.shape.Rectangle backgroundSoftner;

	/**
	 * The screen controller to change of screen
	 */
	private ScreenController controller;

	/**
	 * The play button to turn {@link #game} to the RUNNING STATE
	 */
	@FXML
	private Button play;

	/**
	 * The reset button
	 */
	@FXML
	private Button reset;

	/**
	 * Button to delete the an object
	 */
	@FXML
	private Button delete;

	/**
	 * Button to exit
	 */
	@FXML
	private Button exit;

	/**
	 * Initializes the entire view, the listeners, the backgrounds, the events
	 * and the properties.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.scrollPane.setCache(true);
		this.scrollPane.setCacheHint(CacheHint.SPEED);

		// The header map
		this.headers = new HashMap<>();

		// Creates the list of wrappers contained in the view
		children = FXCollections.observableArrayList();

		// Creates the game and set its boundaries
		this.game = new GoldbergGame();
		game.statusProperty().addListener((value, old, newv) -> handleStatusChanged());
		// Switch to building state
		game.setStatus(GoldbergGame.Status.PREPARING);
		// Default button value
		handleStatusChanged();

		// Binds the planet background to the available width and height
		this.planetBackground.prefWidthProperty().bind(background.widthProperty().subtract(rightItems.widthProperty()));
		this.planetBackground.prefHeightProperty()
				.bind(background.heightProperty().subtract(bottomItems.heightProperty()));

		// Creates the default effect for the clicked wrapper
		selectedEffect = new DropShadow(100, Color.LIGHTGREEN);
		Light light = new Light.Distant(40, 40, Color.LIGHTGREEN);
		Lighting lightning = new Lighting(light);
		selectedEffect.setInput(lightning);

		// Initialize numberfields , listview and rotationSlider
		initNumberFields();
		initListView();
		initRotateSlider();

		// Sets the param label to invisible
		param.setVisible(false);

		// The game's visual bounds converted in pixels.
		heightInPixels = Bindings.createDoubleBinding(() -> ScaleManager.metersToPixels(this.game.getHeight()),
				game.heightProperty());
		widthInPixels = Bindings.createDoubleBinding(() -> ScaleManager.metersToPixels(this.game.getWidth()),
				game.widthProperty());

		// Creates the background softner
		this.backgroundSoftner = new javafx.scene.shape.Rectangle(1, 1, Color.WHITE);
		this.backgroundSoftner.setOpacity(0.8);
		this.backgroundSoftner.widthProperty()
				.bind((widthInPixels.add(2 * ScaleManager.metersToPixels(DynamicWorld.BOUNDS_WIDTH)))
						.multiply(ScaleManager.SCALE));
		this.backgroundSoftner.heightProperty()
				.bind((heightInPixels.add(2 * ScaleManager.metersToPixels(DynamicWorld.BOUNDS_WIDTH)))
						.multiply(ScaleManager.SCALE));
		this.backgroundSoftner.translateXProperty().bind((widthInPixels.divide(2)).multiply(ScaleManager.SCALE));
		this.backgroundSoftner.translateYProperty().bind((heightInPixels.divide(-2)).multiply(ScaleManager.SCALE));

		this.paneParent.getChildren().add(backgroundSoftner);
		backgroundSoftner.toBack();

		// Gives the pane a scale to put the (0,0) point of the bottom left
		// corner of the screen.
		Scale scale = new Scale();
		scale.setX(1);
		scale.setY(-1);
		mainPane.getTransforms().add(scale);

		// Binds its scale to the SCALE MANAGER
		mainPane.scaleXProperty().bind(ScaleManager.SCALE);
		mainPane.scaleYProperty().bind(ScaleManager.SCALE);

		// obligated
		rightItems.setPickOnBounds(false);
		bottomItems.setPickOnBounds(false);

		// Initializes the borders of the world as imageviews
		initBorders();

		// Creates the game over property
		this.game.isOverProperty().addListener((value, old, newv) -> {
			if (newv) {
				ButtonType result = null;
				if (game.hasWon()) {
					result = Dialogs.showEndDialog(State.WIN);

				} else {
					result = Dialogs.showEndDialog(State.LOST);
				}

				if (result == Dialogs.TRY_AGAIN) {
					try {
						reset(null);
					} catch (Exception e) {

					}
				} else if (result == Dialogs.MAIN_MENU) {
					controller.setScreen(Screens.MAIN_MENU);
				} else if (result == Dialogs.LEVEL_CHOICE) {
					controller.setPreviousScreen();
				}
			}
		});
	}

	/**
	 * Initialize the drag event on the listView. When dragged, the list view is
	 * going to perform wrapper generation and add the object to the view.
	 */
	private void initListView() {

		// Creates the cellFactory for the listView
		inventoryListView.setCellFactory(new Callback<ListView<InventoryItem>, ListCell<InventoryItem>>() {

			@Override
			public ListCell<InventoryItem> call(ListView<InventoryItem> param) {
				// TODO Auto-generated method stub
				return new InventoryListCell();
			}
		});

		// Displays the objects
		inventoryListView.setItems(FXCollections.observableArrayList(game.getBuildingInventory().getItems()));

		/*
		 * The drag event on the listView. Once the drag is detected, we create
		 * a new wrapper containing the object clicked from the view if
		 * possible. The drag event is then detected on the listView, but
		 * detected as made on the object.
		 */
		inventoryListView.setOnDragDetected((event) -> {

			if (this.inventoryListView.getSelectionModel().getSelectedItem() != null) {

				// If we can pick an item
				if (this.inventoryListView.getSelectionModel().getSelectedItem().decrement()) {
					// Detect drag events
					this.inventoryListView.startFullDrag();

					// creates the wrapper
					ObservableWrapper wrapper = getSelectedListViewWrapper();

					if (wrapper != null) {
						if (clickedWrapper != null) {
							clickedWrapper.view.setEffect(null);
						}

						// Adds the new object to the game and the children
						this.game.addGameObject(wrapper);
						this.children.add(wrapper);

						// Adds to the view
						this.mainPane.getChildren().add(wrapper.view);
						wrapper.view.setEffect(selectedEffect);
						wrapper.view.setPickOnBounds(false);
						wrapper.view.toBack();

						// Start dragging event and set controller
						wrapper.controller.setParentController(this);
						this.clickedWrapper = wrapper;
						isDragged = true;

						// Sets the wrapper to the position required
						this.clickedWrapper.observable.translate(5, 5);
						this.game.setDraggedWrapper(wrapper);
						this.clickedWrapper.observable.translate(ScaleManager.pixelToMeters(event.getX()),
								ScaleManager.pixelToMeters(event.getY()));

						// Changes the value of the rotation slider to the new
						// object's
						rotationSlider.setValue(Math.toDegrees(clickedWrapper.observable.getRotate()));

						// Displays the header
						displayHeader();

						// Makes controls usable
						posX.setDisable(false);
						posY.setDisable(false);
						rotationSlider.setDisable(false);

					}
				}
			}
		});
		this.inventoryListView.setOnDragDone((event) -> {
			mouseDragEnded(null);
		});
	}

	/**
	 * Initialize the binding associated to the slider's value. The binding is
	 * only performed when the slider is focused.
	 */
	private void initRotateSlider() {
		/*
		 * Add listeners of the rotationSlider
		 */
		rotationListener = ((value, old, newv) -> {
			if (clickedWrapper != null) {
				clickedWrapper.observable.rotate(Math.toRadians(newv.doubleValue()));
				clickedWrapper.view.setEffect(selectedEffect);
			}
		});

		rotationSlider.focusedProperty().addListener((value, old, newv) -> {
			if (newv) {
				this.rotationSlider.valueProperty().addListener(rotationListener);
			} else {
				this.rotationSlider.valueProperty().removeListener(rotationListener);
			}
		});

		/*
		 * Binds rotation label
		 */
		rotateLabel.textProperty().bind(rotationSlider.valueProperty().asString("%.0f" + "\u00B0"));

		// Sets the default controllers to disable
		rotationSlider.setDisable(true);
	}

	/**
	 * Insert the number fields in the view, and initialize them
	 */
	private void initNumberFields() {
		// Creates the NumberFields for position
		posX = new NumberField();
		posY = new NumberField();
		positionGrid.add(posX, 0, 1);
		positionGrid.add(posY, 1, 1);

		// Add the listeners of the orientation
		textInputListener = ((value, old, newv) -> {
			if (clickedWrapper.observable != null) {
				clickedWrapper.observable.translate(posX.getValue(), posY.getValue());
			}
		});

		/*
		 * Add the listener on the focused property of the textFields
		 */
		posX.focusedProperty().addListener((value, old, newv) -> {
			if (newv) {
				posX.valueProperty().addListener(textInputListener);
			} else {
				posX.valueProperty().removeListener(textInputListener);
			}
		});
		posY.focusedProperty().addListener((value, old, newv) -> {
			if (newv) {
				posY.valueProperty().addListener(textInputListener);
			} else {
				posY.valueProperty().removeListener(textInputListener);
			}
		});

		// Sets the default controllers to disable
		posX.setDisable(true);
		posY.setDisable(true);
	}

	/**
	 * Changes the view when the game's status is changed.
	 * 
	 * If the new status is PREPARING, then the tools are set to be used
	 * 
	 * Otherwise, the only button available is paused, to stop the current
	 * thread of updating.
	 */
	private void handleStatusChanged() {
		if (game.getStatus() == GoldbergGame.Status.PREPARING) {
			this.reset.setDisable(true);
			this.play.setDisable(false);
			this.delete.setDisable(false);
			((ImageView) this.play.getGraphic()).setImage(PLAY);
			this.status.setText("Status : Préparation");
			inventoryListView.setDisable(false);
		} else {

			this.reset.setDisable(false);
			this.play.setDisable(false);
			this.delete.setDisable(true);
			((ImageView) this.play.getGraphic()).setImage(PAUSE);
			this.status.setText("Status : En jeu");
			inventoryListView.setDisable(true);

		}
	}

	/**
	 * Returns an observable wrapper associated to the Selected inventory item
	 * in the listView. Generates the items using {@link #factory}, and using
	 * Setting's default parameter as parameters.
	 */
	private ObservableWrapper getSelectedListViewWrapper() {

		return Settings.getDefaultWrapper(inventoryListView.getSelectionModel().getSelectedItem().getItemType());
	}

	/**
	 * Initialize the world's borders as imageViews, for the user to be able to
	 * see them. The borders are set differently, depending on if the world is
	 * bounded, crossed teleportable, or not bounded.
	 */
	private void initBorders() {

		final double boundsWidthInPixels = ScaleManager.metersToPixels(DynamicWorld.BOUNDS_WIDTH);

		top = new ImageView();
		right = new ImageView();
		bottom = new ImageView();
		left = new ImageView();

		top.setPickOnBounds(false);
		right.setPickOnBounds(false);
		bottom.setPickOnBounds(false);
		left.setPickOnBounds(false);

		// Do not allow preserveRatio
		top.setPreserveRatio(false);
		right.setPreserveRatio(false);
		bottom.setPreserveRatio(false);
		left.setPreserveRatio(false);

		// Binds the imageView's translations to the world's bounds
		top.setFitHeight(boundsWidthInPixels);
		top.fitWidthProperty().bind(widthInPixels.add(boundsWidthInPixels * 2));
		top.setLayoutX(-boundsWidthInPixels);
		top.layoutYProperty().bind(heightInPixels);
		top.setRotate(180);

		right.setFitWidth(boundsWidthInPixels);
		right.fitHeightProperty().bind(heightInPixels.add(boundsWidthInPixels));
		right.setLayoutY(-boundsWidthInPixels);
		right.layoutXProperty().bind(widthInPixels);
		left.setRotate(180);

		bottom.setFitHeight(boundsWidthInPixels);
		bottom.fitWidthProperty().bind(widthInPixels.add(boundsWidthInPixels));
		bottom.setLayoutX(-boundsWidthInPixels);
		bottom.layoutYProperty().bind(bottom.fitHeightProperty().multiply(-1));

		left.setFitWidth(boundsWidthInPixels);
		left.fitHeightProperty().bind(heightInPixels.add(boundsWidthInPixels));
		left.layoutXProperty().bind(left.fitWidthProperty().multiply(-1));
		left.setRotate(180);

		top.toFront();
		right.toFront();
		bottom.toFront();
		left.toFront();

		setBoundsImage();

		// Listener on boundsCrossedTeleportation value
		game.boundsProperty().addListener((value, old, newv) -> {
			this.setBoundsImage();
		});
	}

	/**
	 * Sets the appropriate bounds image, depending on the world's bounds
	 * properties. If the world in bounded, the the bounds are set to the Yellow
	 * and black stripped walls, or has the black clouds if the world in
	 * crossedTeleportable.
	 */
	private void setBoundsImage() {
		Image horizontal = null;
		Image vertical = null;

		if (game.getBounds() == BorderType.NORMAL) {
			horizontal = new Image(getClass().getResource("/images/Border_Horizontal.png").toExternalForm());
			vertical = new Image(getClass().getResource("/images/Border_Vertical.png").toExternalForm());
		} else if (game.getBounds() == BorderType.TELEPORTABLE) {
			horizontal = new Image(getClass().getResource("/images/teleportable_horizontal.jpg").toExternalForm());
			vertical = new Image(getClass().getResource("/images/teleportable_vertical.jpg").toExternalForm());
		}
		top.setImage(horizontal);
		bottom.setImage(horizontal);

		left.setImage(vertical);
		right.setImage(vertical);

		if (!mainPane.getChildren().contains(top)) {
			mainPane.getChildren().addAll(top, right, bottom, left);
		}
	}

	/**
	 * Allows the user to exit the builder's view and to get back to Main_Menu
	 * 
	 * @param event
	 */
	@FXML
	private void exit(ActionEvent event) {
		try {
			ButtonType result = Dialogs.showDialog(null,
					"Voulez-vous vraiment quitter ? Toute progression sera perdue...", AlertType.CONFIRMATION);
			if (result == ButtonType.OK) {
				controller.setScreen(Screens.MAIN_MENU);
			}

		} catch (UnsupportedDataTypeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If the game is currently running, this method is going to set the world
	 * into pause state. If the world is in preaparing state, this method will
	 * put the game into RUNNING state, and collisions will start. User won't be
	 * allowed to modify the world from there.
	 * 
	 * @param event
	 */
	@FXML
	private void play(ActionEvent event) {

		if (game.getStatus() == GoldbergGame.Status.PREPARING) {

			posX.setDisable(true);
			posY.setDisable(true);
			rotationSlider.setDisable(true);

			this.clickedWrapper = null;
			displayHeader();
			try {
				game.setStatus(GoldbergGame.Status.RUNNING);
			} catch (Exception e) {

				try {
					Dialogs.showDialog(null,
							"Impossible de construire le niveau : " + " Il y a des objets qui sont en collisions",
							AlertType.ERROR);
				} catch (UnsupportedDataTypeException e1) {

					e1.printStackTrace();
				}

			}
			game.start();
		} else if (game.isRunning()) {
			game.stop();
			reset.setDisable(true);
			((ImageView) this.play.getGraphic()).setImage(PLAY);
			this.status.setText("Status : Pause");

		} else {
			game.start();
			reset.setDisable(false);
			((ImageView) this.play.getGraphic()).setImage(PAUSE);
			this.status.setText("Status : En jeu");

		}
	}

	/**
	 * Resets the game completely. Only works when RUNNING.
	 * 
	 * @param event
	 */
	@FXML
	private void reset(ActionEvent event) {
		game.reset();
	}

	/**
	 * Deletes all the fixed objects from the game. This method must only
	 * removes the fixed items from the game, clear their views from the
	 * mainPane.
	 * 
	 * @param event
	 */
	@FXML
	private void delete(ActionEvent event) {
		deleteFocusedWrapper();
	}

	/**
	 * Removes the currently focus object from panes, game, level, and current
	 * children list. Completely removes it from the current world.
	 */
	private void deleteFocusedWrapper() {

		if (clickedWrapper != null) {
			// Removes it from everywhere
			children.remove(clickedWrapper);
			game.removeGameObject(clickedWrapper);
			mainPane.getChildren().remove(clickedWrapper.view);
			level.removeFixedObject(clickedWrapper.observable);

			// Put the item back again in the backpack
			level.getInventory().getItem(clickedWrapper.instance).increment();

			this.clickedWrapper = null;
			this.displayHeader();
			System.gc();

		}

	}

	/**
	 * Called when an object is clicked and the drag enter the mainPane
	 * 
	 * @param event
	 */
	@FXML
	private void mouseDragStarted(MouseEvent event) {

		if (clickedWrapper != null && isDragged) {
			clickedWrapper.observable.translate(ScaleManager.pixelToMeters(event.getX()),
					ScaleManager.pixelToMeters(event.getY()));
			posX.setValue(clickedWrapper.observable.getTranslate().x);
			posY.setValue(clickedWrapper.observable.getTranslate().y);
			clickedWrapper.view.setEffect(selectedEffect);
		}
	}

	/**
	 * Called when a drag gesture is released over {@link #mainPane}
	 * 
	 * @param event
	 */
	@FXML
	private void mouseDragEnded(MouseEvent event) {
		if (!game.getStatus().equals(GoldbergGame.Status.RUNNING)) {

			isDragged = false;
			game.dropWrapper();
		}

	}

	/**
	 * Called when the mouse is released over {@link #mainPane}
	 * 
	 * @param event
	 */
	@FXML
	private void mouseReleased(MouseEvent event) {

		if (!game.getStatus().equals(GoldbergGame.Status.RUNNING)) {

			// Notifies the game to drop the wrapper. If it still collides, the
			// wrapper is replaced.
			isDragged = false;
			game.dropWrapper();
		}
	}

	/**
	 * Called by {@link #mainPane} on key pressed.
	 * 
	 * Simply deletes the focused wrapper if the currently focused wrapper is
	 * not null and the key is DELETE
	 * 
	 * @param event
	 */
	@FXML
	private void keyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			if (clickedWrapper != null) {
				deleteFocusedWrapper();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScreenController(ScreenController SC) {
		this.controller = SC;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayedToScreen() {
		SoundMaker.playSong(GameSong.BUILDER);
		game.start();
	}

	/**
	 * Sets the ideal scroll to represent the world's bounds in the game. Tey
	 * are calculated from the space available between the bounds, and the width
	 * and height of the world in pixels.
	 */
	private void setIdealScroll() {

		// Calculates ideal scale for this level, from its height
		double idealScrollWidth = (background.getPrefWidth() - rightItems.getPrefWidth()) / widthInPixels.get();
		double idealScrollHeight = (background.getPrefHeight() - bottomItems.getPrefHeight()) / heightInPixels.get();

		// Chose the minimal scroll
		ScaleManager.setScale(Math.min(idealScrollHeight, idealScrollWidth));

	}

	/**
	 * Sets the level of the builder to the one in parameter. Ensures to load
	 * all the objects and display corresponding headers to the appropriate
	 * places.
	 * 
	 * @param level
	 */
	public void setLevel(Level level) {

		// Changes the level everywhere
		this.level = level;
		this.children.clear();
		this.mainPane.getChildren().clear();
		this.game.setStatus(GoldbergGame.Status.PREPARING);
		this.game.setLevel(level);

		// Changes the background
		Background planetBack = new Background(new BackgroundImage(new Image(level.getPlanet().getPicture()),
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(Double.MAX_VALUE, Double.MAX_VALUE, true, true, true, true)));

		this.planetBackground.setBackground(planetBack);

		// Generates the wrappers
		this.children = FXCollections.observableArrayList();

		// Changes the inventory for the new one
		final Inventory inventory = game.getAllowedInventory();
		if (inventory != null) {
			inventoryListView.setItems(FXCollections.observableArrayList(game.getAllowedInventory().getItems()));
		}

		// Put the fixed wrappers in the view.
		final List<ObservableWrapper> fixed = game.getFixeedWrappers();

		for (ObservableWrapper wrapper : fixed) {
			mainPane.getChildren().add(wrapper.view);
			wrapper.controller.setParentController(this);
			wrapper.view.setPickOnBounds(false);
			wrapper.view.toBack();
		}

		// Displays bounds and scroll
		setBoundsImage();
		setIdealScroll();

	}

	/**
	 * reset the game if running, and clears it to release useless wrappers.
	 */
	@Override
	public void removedFromScreen() {
		if (game.getStatus() == GoldbergGame.Status.RUNNING) {
			game.reset();
		}
		game.stop();
		// Removes physic objects from the game, so no exception will be thrown
		// later
		game.clear();
	}

	/**
	 * Called by the view when a scroll event is detected. Will increment the
	 * scroll on the {@link ScaleManager}
	 * 
	 * @param event
	 */
	@FXML
	private void scroll(ScrollEvent event) {

		if (event.isControlDown()) {
			if (event.getDeltaY() > 0) {
				ScaleManager.zoom();
			} else {
				ScaleManager.unZoom();
			}
		}
	}

	/**
	 * Called by every children to notify that the children is pressed. This
	 * method will perform research among the children to find which one has
	 * been clicked to notify the game of the event.
	 * 
	 * If the view is null, then the headers are set to disable
	 */
	@Override
	public void notifyChildrenPressed(AbstractComplexObservable object, Observable instance, ChildrenController source,
			MouseEvent event) {

		if (game.getStatus() != GoldbergGame.Status.RUNNING) {

			isDragged = true;

			// Turns old clicked to normal color
			if (clickedWrapper != null) {
				clickedWrapper.view.setEffect(null);
			}
			if (object != null) {

				posX.setDisable(true);
				posY.setDisable(true);
				rotationSlider.setDisable(true);

				// Before searching, we put the actual clicked to null,
				// considering
				// a fixed object could have been clicked
				clickedWrapper = null;

				// research the view clicked in the gameComponents, ignoring
				// clicks
				// on fixed objects
				for (ObservableWrapper wrapper : children) {
					if (wrapper.observable == object) {

						// Notifies the game of the dragged object
						this.clickedWrapper = wrapper;
						game.setDraggedWrapper(clickedWrapper);

						// turn clicked object to green
						clickedWrapper.view.setEffect(selectedEffect);

						posX.setDisable(false);
						posY.setDisable(false);
						rotationSlider.setDisable(false);

						break;
					}
				}

				// Whatever the object clicked, we display a corresponding
				// header.
				// If the clicked object is null, no header in applied.
				displayHeader();

				// Manage to remove the listeners from the focused input control
				// from the view, in order to change the value, and put the
				// listeners back on if there were some.
				boolean hadPosXListener = posX.isFocused();
				boolean hadPosYListener = posY.isFocused();
				boolean hadRotateListener = rotationSlider.isFocused();

				posX.valueProperty().removeListener(textInputListener);
				posY.valueProperty().removeListener(textInputListener);
				rotationSlider.valueProperty().removeListener(rotationListener);

				posX.setValue(object.getTranslate().x);
				posY.setValue(object.getTranslate().y);
				rotationSlider.setValue((Math.toDegrees(object.getRotate()) + 10 * 360) % 360);

				if (hadPosXListener) {
					posX.valueProperty().addListener(textInputListener);
				} else if (hadPosYListener) {
					posY.valueProperty().addListener(textInputListener);
				} else if (hadRotateListener) {
					rotationSlider.valueProperty().addListener(rotationListener);
				}

			} else {
				posX.setDisable(true);
				posY.setDisable(true);
				rotationSlider.setDisable(true);
			}
		}
	}

	/**
	 * Displays the header corresponding to the instance of the currently
	 * clicked wrapper.
	 */
	private void displayHeader() {
		if (clickedWrapper != null) {

			if (headers.get(clickedWrapper.instance) == null && clickedWrapper.instance.getHeaderPath() != null) {
				generateHeader(clickedWrapper.instance);
				displayHeader();
			} else if (headers.get(clickedWrapper.instance) != null) {

				HeaderViewWrapper wrapper = headers.get(clickedWrapper.instance);
				wrapper.controller.setObservable(clickedWrapper.observable);

				/*
				 * Changes the actual header view to the new one
				 */
				if (headerWrapper.getChildren().isEmpty()) {
					headerWrapper.getChildren().add(wrapper.view);
				} else {
					headerWrapper.getChildren().set(0, wrapper.view);
				}

			} else {
				headerWrapper.getChildren().clear();
			}
		} else {
			headerWrapper.getChildren().clear();
		}

		if (headerWrapper.getChildren().isEmpty()) {
			param.setVisible(false);
		} else {
			param.setVisible(true);
		}
	}

	/**
	 * Generate the header of the instance in parameter. This method requires
	 * that the header is not already load to perform the work.
	 * 
	 * @param instance
	 *            The object instance of the header to load.
	 */
	private void generateHeader(Observable instance) {
		if (instance.getHeaderPath() != null) {

			HeaderViewWrapper header = new HeaderViewWrapper();

			FXMLLoader loader = new FXMLLoader(instance.getHeaderPath());
			try {
				header.view = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			header.controller = loader.getController();
			headers.put(instance, header);
		}
	}

	/**
	 * 
	 * @return The current menu's level
	 */
	public Level getLevel() {
		return level;
	}

}
