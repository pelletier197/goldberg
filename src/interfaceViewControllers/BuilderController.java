package interfaceViewControllers;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.activation.UnsupportedDataTypeException;
import javax.imageio.ImageIO;

import game.BorderType;
import game.GameSong;
import game.GoldbergGame;
import game.InventoryItem;
import game.Level;
import game.Settings;
import game.SoundMaker;
import gameObservableControllers.ChildrenController;
import gameObservableControllers.ParentController;
import gameObservableViews.InventoryListCell;
import gameObservableViews.ObservableWrapper;
import gameObservables.Observable;
import interfaceViewControllers.headers.HeaderViewWrapper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
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
import views.screenController.ScreenController.Animations;
import views.screenController.Screens;

public class BuilderController implements Initializable, ControlledScreen, ParentController {

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
	 * The inventory dipslayed in the bottom of the screen
	 */
	@FXML
	private ListView<InventoryItem> inventoryListView;

	/**
	 * The right items, holding the tools for moving/rotating
	 */
	@FXML
	private VBox rightItems;

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
	 * The last backgroun of the center, used to the display the planet's
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
	 * Initializes the entire view, the listeners, the backgrounds, the events
	 * and the properties.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// The header map
		this.headers = new HashMap<>();
		// Creates the list of wrappers contained in the view
		children = FXCollections.observableArrayList();

		// Creates the game and set its boundaries
		this.game = new GoldbergGame();
		// Switch to building state
		game.setStatus(GoldbergGame.Status.BUILDING);

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

		// Sets param to invisible
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

		// obligated for every object inserted in the view
		rightItems.setPickOnBounds(false);
		bottomItems.setPickOnBounds(false);

		// Initializes the borders of the world as imageviews
		initBorders();

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
						this.game.addFixedObject(wrapper);
						this.children.add(wrapper);
						this.level.addFixedObject(wrapper.observable);

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
	 * The finished button, that indicates the Level is complete, and can be
	 * saved as placed. The game could possibly not be loaded and saved, because
	 * some objects could be wrongly placed.
	 * 
	 * @param event
	 */
	@FXML
	private void finish(ActionEvent event) {

		// If the game can build a level, than all restriction are passed
		if (game.canBuild()) {

			// Resizes the game to the perfect scroll the be able to take a
			// screenshot of the game.
			Task<Void> resizer = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					setIdealScroll();
					Thread.yield();
					Thread.sleep(100);
					return null;
				}
			};

			// Operates a screenshot and saves it to the level.
			resizer.setOnSucceeded((state) -> {
				try {
					// Creates a robot that will handle screenshot
					Robot robot = new Robot();
					final javafx.geometry.Bounds bounds = left.getBoundsInLocal();
					final javafx.geometry.Bounds screenBounds = left.localToScreen(bounds);
					BufferedImage image = robot.createScreenCapture(new Rectangle((int) screenBounds.getMinX(),
							(int) screenBounds.getMinY(),
							(int) ((widthInPixels.get() + right.getFitWidth() * 2) * ScaleManager.SCALE.get()),
							(int) ((heightInPixels.get() + bottom.getFitHeight() * 2) * ScaleManager.SCALE.get())));

					// Converts image to a byte array and assigns it to the
					// level
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(image, "jpg", baos);
					baos.flush();
					level.setScreenShot(baos.toByteArray());
					baos.close();
				} catch (AWTException | IOException e) {
					e.printStackTrace();
				}
				controller.setScreen(Screens.CHOSE_OBJECT, Animations.TRANSLATE_LEFT_TO_CENTER);
			});

			new Thread(resizer).start();
		} else {

			// If the world can't build, we verify every case and tell the user
			// why
			String msg = "";
			if (!game.hasCoins()) {
				msg += "\t - Il faut au moins une pièce\n";
			}
			if (!game.hasPots()) {
				msg += "\t - Il faut au moins un pot d'or\n";
			}
			if (game.stillHaveCollisions()) {
				msg += "\t - Il y a des objets qui sont en collisions\n";
			}
			try {
				Dialogs.showDialog(null, "Impossible de construire le niveau : \n" + msg, AlertType.ERROR);
			} catch (UnsupportedDataTypeException e) {
			}
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
		isDragged = false;
		game.dropWrapper();
	}

	/**
	 * Called when the mouse is released over {@link #mainPane}
	 * 
	 * @param event
	 */
	@FXML
	private void mouseReleased(MouseEvent event) {

		// Notifies the game to drop the wrapper. If it still collides, the
		// wrapper is replaced.
		isDragged = false;
		game.dropWrapper();
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
			deleteFocusedWrapper();
		}
	}

	/**
	 * Removes the currently focus object from panes, game, level, and current
	 * children list. Completely removes it from the current world.
	 */
	private void deleteFocusedWrapper() {
		if (clickedWrapper != null) {

			// Removes it from everywhere
			children.remove(clickedWrapper);
			game.removeFixedObject(clickedWrapper);
			mainPane.getChildren().remove(clickedWrapper.view);
			level.removeFixedObject(clickedWrapper.observable);

			// Add the item again to the inventory if deleted
			for (InventoryItem item : inventoryListView.getItems()) {
				if (item.getItemType() == clickedWrapper.instance) {
					item.increment();
					break;
				}
			}

			// Display required headers
			this.clickedWrapper = null;
			this.displayHeader();

			System.gc();
		}
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
		// Dragged by default
		isDragged = true;

		// Turns previously clicked to normal color
		if (clickedWrapper != null) {
			clickedWrapper.view.setEffect(null);
		}
		if (object != null) {

			// disables options
			posX.setDisable(false);
			posY.setDisable(false);
			rotationSlider.setDisable(false);

			// research the view clicked
			for (ObservableWrapper wrapper : children) {
				if (wrapper.observable == object) {

					// Notifies the game of the dragged object
					this.clickedWrapper = wrapper;
					game.setDraggedWrapper(clickedWrapper);

					// turn clicked object to green
					clickedWrapper.view.setEffect(selectedEffect);

					displayHeader();
					break;
				}
			}

			posX.setValue(object.getTranslate().x);
			posY.setValue(object.getTranslate().y);
			rotationSlider.setValue(Math.toDegrees(object.getRotate()));
		} else {
			posX.setDisable(true);
			posY.setDisable(true);
			rotationSlider.setDisable(true);
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
	 * @return The current builder's level
	 */
	public Level getLevel() {
		// TODO Auto-generated method stub
		return level;
	}

	/**
	 * Sets the level of the builder to the one in parameter. Ensures to load
	 * all the objects and display corresponding headers to the appropriate
	 * places.
	 * 
	 * @param level
	 */
	public void setLevel(Level level) {

		// Changes of level everywhere
		this.level = level;
		this.children.clear();
		this.mainPane.getChildren().clear();
		this.game.setLevel(level);

		// Displays the objects back on
		inventoryListView.setItems(FXCollections.observableArrayList(game.getBuildingInventory().getItems()));
		// Generates the wrappers
		this.children = FXCollections.observableArrayList(game.getFixeedWrappers());

		// Changes the background
		Background planetBack = new Background(new BackgroundImage(new Image(level.getPlanet().getPicture()),
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(Double.MAX_VALUE, Double.MAX_VALUE, true, true, true, true)));

		this.planetBackground.setBackground(planetBack);

		// add the wrapper to the view, respecting restrictions
		for (ObservableWrapper wrapper : children) {
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
	public void removedFromScreen() {
		game.stop();

		// Removes physic objects from the game, so no exception will be thrown
		// later
		game.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayedToScreen() {
		SoundMaker.playSong(GameSong.BUILDER);
		game.start();

	}
}
