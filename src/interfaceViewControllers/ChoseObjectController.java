package interfaceViewControllers;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import game.Inventory;
import game.InventoryItem;
import game.Level;
import gameObservables.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import main.IntegerField;
import utils.BufferingUtils;
import views.screenController.ControlledScreen;
import views.screenController.ScreenController;
import views.screenController.ScreenController.Animations;
import views.screenController.Screens;

public class ChoseObjectController implements ControlledScreen, Initializable {

	/**
	 * The screen controller
	 */
	private ScreenController screenController;

	/**
	 * The final level generated by the view.
	 */
	private Level newLevel;

	@FXML
	private IntegerField springQuantity;

	@FXML
	private IntegerField ropeQuantity;

	@FXML
	private IntegerField basculeQuantity;

	@FXML
	private IntegerField stickBasculeQuantity;

	@FXML
	private IntegerField surfaceQuantity;

	@FXML
	private IntegerField dominoQuantity;

	@FXML
	private Button boutonTerminer;

	@FXML
	private StackPane imagePane;

	@FXML
	private ImageView image;

	/**
	 * Called by the view when the finish button is pressed by the user. Will
	 * generate the level with the inventory specified in the view, then save it
	 * using {@link BufferingUtils}
	 * 
	 * @param event
	 */
	@FXML
	private void clickedButton(MouseEvent event) {

		createIventory();
		saveLevel();
		comeMainMenu();
	}

	/**
	 * Gets the level from the previous builder view.
	 */
	private void generateLevel() {
		newLevel = (((BuilderController) screenController.getController(Screens.BUILDER))).getLevel();
	}

	/**
	 * This method creates the Inventory of the level with the Numberfieds'
	 * information
	 */
	private void createIventory() {
		Inventory inventory = new Inventory();
		// ajouter la quantit?? apr??s avoir modifier le type de contenant

		inventory.addItem(new InventoryItem(Observable.SPRING, (int) springQuantity.getValue()));

		inventory.addItem(new InventoryItem(Observable.ROPE, (int) ropeQuantity.getValue()));

		inventory.addItem(new InventoryItem(Observable.BASCULE, (int) basculeQuantity.getValue()));

		inventory.addItem(new InventoryItem(Observable.SURFACE, (int) surfaceQuantity.getValue()));

		inventory.addItem(new InventoryItem(Observable.STICK_BASCULE, (int) stickBasculeQuantity.getValue()));

		inventory.addItem(new InventoryItem(Observable.DOMINO, (int) dominoQuantity.getValue()));

		newLevel.setInventory(inventory);
	}

	/**
	 * Save the new level to a file
	 */
	private void saveLevel() {
		BufferingUtils.saveLevel(newLevel);
	}

	/**
	 * come back to MainMenu
	 */
	private void comeMainMenu() {

		screenController.setScreen(Screens.MAIN_MENU, Animations.TRANSLATE_BOTTOM_TO_CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScreenController(ScreenController SC) {
		screenController = SC;
	}

	/**
	 * Will get the level from the from previous screen and display the
	 * screenshot.
	 */
	@Override
	public void displayedToScreen() {
		// generates the current level from the builder screen
		generateLevel();

		// Displays its image
		image.setImage(new Image(new ByteArrayInputStream(newLevel.getScreenShot())));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removedFromScreen() {

	}

	/**
	 * Initializes the Numberfields
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		springQuantity.setMax(15);
		ropeQuantity.setMax(15);
		basculeQuantity.setMax(15);
		surfaceQuantity.setMax(50);
		dominoQuantity.setMax(50);

		springQuantity.setMin(0);
		ropeQuantity.setMin(0);
		basculeQuantity.setMin(0);
		surfaceQuantity.setMin(0);
		dominoQuantity.setMin(0);

	}
}
