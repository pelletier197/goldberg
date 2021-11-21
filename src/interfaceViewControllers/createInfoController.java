package interfaceViewControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import game.BorderType;
import game.Level;
import game.Planet;
import game.Settings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import main.NumberField;
import views.screenController.ControlledScreen;
import views.screenController.ScreenController;
import views.screenController.ScreenController.Animations;
import views.screenController.Screens;

public class createInfoController implements ControlledScreen, Initializable {

	/**
	 * Image of the borders for the game.
	 */
	private static final Image BORDERS = new Image(
			createInfoController.class.getResource("/images/borders.png").toExternalForm());

	/**
	 * Image of the teleportable borders for the game.
	 */
	private static final Image TELEPORTABLE_BORDERS = new Image(
			createInfoController.class.getResource("/images/teleportable_borders.png").toExternalForm());
	@FXML
	private TextField levelName;
	@FXML
	private TextField creator;
	@FXML
	private Button previous;
	@FXML
	private Button suivant;
	@FXML
	private Label title;
	@FXML
	private StackPane mapVisualizer;
	@FXML
	private ImageView background;
	@FXML
	private ImageView borders;
	@FXML
	private ComboBox<Planet> planetBox;
	@FXML
	private GridPane sizeGrid;
	@FXML
	private RadioButton withBorderRadio;
	@FXML
	private RadioButton teleportableBordrersRadio;

	/**
	 * Toggle for the border's choice
	 */
	private ToggleGroup boundsToggle;

	/**
	 * The screen controller
	 */
	private ScreenController controller;

	/**
	 * Number field for height
	 */
	private NumberField height;

	/**
	 * Number field for width
	 */
	private NumberField width;

	/**
	 * The level that the view will generate.
	 */
	private Level level;

	/**
	 * Initializes the comboBox changing and the borders changing for the view
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/*
		 * Creates the toggleGroup for the choice of bounds
		 */
		boundsToggle = new ToggleGroup();
		withBorderRadio.setToggleGroup(boundsToggle);
		teleportableBordrersRadio.setToggleGroup(boundsToggle);

		boundsToggle.selectedToggleProperty().addListener((value, old, newv) -> {
			setBorderImages();
		});

		/*
		 * Creates the numbers fields
		 */
		this.height = new NumberField(20, 500);
		this.width = new NumberField(20, 500);
		this.height.setValue(50);
		this.width.setValue(50);
		this.sizeGrid.add(width, 0, 1);
		this.sizeGrid.add(height, 1, 1);
		this.width.setMaxWidth(150);
		this.height.setMaxWidth(150);
		this.levelName.requestFocus();

		/*
		 * Creates the planet choice box
		 */
		planetBox.setItems(FXCollections.observableArrayList(Settings.getBuildingPlanets()));
		planetBox.getSelectionModel().select(planetBox.getItems().get(0));
		planetBox.setCellFactory(new Callback<ListView<Planet>, ListCell<Planet>>() {

			@Override
			public ListCell<Planet> call(ListView<Planet> param) {

				return new ListCell<Planet>() {

					@Override
					protected void updateItem(Planet item, boolean empty) {
						super.updateItem(item, empty);

						if (item != null && !empty) {
							ImageView image = new ImageView(new Image(item.getPicture(), true));
							image.setFitHeight(25);
							image.setFitWidth(25);
							this.setHeight(30);
							setGraphic(image);
							setText(item.getName() + " (g = " + String.format("%1.1f", item.getGravity()) + " m/s²)");
						}
					}

				};
			}
		});
		planetBox.getSelectionModel().selectedItemProperty().addListener((value, old, newv) -> {
			setPlanetImage();
		});

		/*
		 * Proportion the size of the height and width of the borders
		 */
		// Square background image
		this.background.fitWidthProperty().bind(mapVisualizer.widthProperty().subtract(200));
		this.background.fitHeightProperty().bind(background.fitWidthProperty());

		// square borders image
		this.borders.fitWidthProperty().bind(background.fitWidthProperty().subtract(30));
		this.borders.fitHeightProperty().bind(background.fitHeightProperty().subtract(30));

		setBorderImages();
		setPlanetImage();
	}

	/**
	 * Sets the planet image according to the actual planet choice of item.
	 */
	private void setPlanetImage() {
		Planet newv = planetBox.getSelectionModel().getSelectedItem();
		if (newv != null) {
			background.setImage(new Image(newv.getPicture()));
		} else {
			background.setImage(null);
		}
	}

	/**
	 * Sets the image of the border matching the actual choice of borders.
	 */
	private void setBorderImages() {
		Toggle newv = boundsToggle.getSelectedToggle();
		if (newv == teleportableBordrersRadio) {
			borders.setImage(TELEPORTABLE_BORDERS);
		} else if (newv == withBorderRadio) {
			borders.setImage(BORDERS);
		}
	}

	/**
	 * Called by the view when {@link #previous} is pressed.
	 * 
	 * @param event
	 */
	@FXML
	public void back(ActionEvent event) {
		controller.setScreen(Screens.MAIN_MENU, Animations.TRANSLATE_RIGHT_TO_CENTER);
	}

	/**
	 * Called by the view when {@link #suivant} is pressed. Will charge the
	 * level created in the builder
	 * 
	 * @param event
	 */
	@FXML
	public void next(ActionEvent event) {

		// Generates a serial number to make it unique
		long serialNumber = Settings.generateSerialNumber();
		level = null;

		// Generates the level
		try {
			try {
				// Creates a level matching creator_levelName_serial.god
				level = new Level(levelName.getText(), creator.getText(), Settings.PATH_TO_PERSONNAL + creator.getText()
						+ levelName.getText() + serialNumber + Settings.EXTENSION);
			} catch (Exception e) {
				e.printStackTrace();

				// alternative creation on failure
				level = new Level(levelName.getText(), creator.getText(), Settings.PATH_TO_PERSONNAL
						+ Level.DEFAULT_NAME + Level.DEFAULT_NAME + serialNumber + Settings.EXTENSION);
			}

			// sets the level's planet
			level.setPlanet(planetBox.getSelectionModel().getSelectedItem());

			// sets the borders
			generateBorder(level);

			// sets height and width
			setHeightWidth(height.getValue(), width.getValue(), level);

			// Loads the builder
			controller.setScreen(Screens.BUILDER, Animations.TRANSLATE_LEFT_TO_CENTER);

			// Loads the level into the builder
			BuilderController builderController = (BuilderController) controller.getController(Screens.BUILDER);
			builderController.setLevel(level);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * @return The level generated by the view at this point.
	 */
	public Level getGeneratedLevel() {
		return level;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScreenController(ScreenController SC) {
		this.controller = SC;

	}

	/**
	 * Clears the textfields
	 */
	@Override
	public void displayedToScreen() {
		creator.clear();
		levelName.clear();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removedFromScreen() {

	}

	/**
	 * Generate the borders matching the toggle selection
	 * 
	 * @param level
	 */
	private void generateBorder(Level level) {
		if (withBorderRadio.isSelected()) {
			level.setBorders(BorderType.NORMAL);
		} else if (teleportableBordrersRadio.isSelected()) {
			level.setBorders(BorderType.TELEPORTABLE);
		}
	}

	/**
	 * Sets the height and the width of the level sent in parameter
	 * 
	 * @param heigth
	 *            The height
	 * @param width
	 *            The width
	 * @param level
	 *            The level
	 */
	private void setHeightWidth(double heigth, double width, Level level) {
		level.setHeight(heigth);
		level.setWidth(width);
	}

}
