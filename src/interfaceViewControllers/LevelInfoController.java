package interfaceViewControllers;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import game.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LevelInfoController implements Initializable {

	@FXML
	private ImageView screenshot;
	@FXML
	private Label information;

	/**
	 * Initializes the level view information.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.screenshot.setPreserveRatio(true);
		this.screenshot.setFitHeight(100);
	}

	/**
	 * Sets the view's level. Displays the information this way
	 * 
	 * <li>SCREENSHOT
	 * <li>SCREENSHOT
	 * <li>SCREENSHOT
	 * 
	 * <li>Name - creator
	 * 
	 * @param level
	 *            The level that defines the view.
	 */
	public void setLevel(Level level) {
		try {
			this.screenshot.setImage(new Image(new ByteArrayInputStream(level.getScreenShot())));
		} catch (NullPointerException e) {
			// Happens if an error occured during construction
		}
		this.information.setText(level.getName() + " - " + level.getCreator());
	}

	/**
	 * Called when the view is clicked
	 * 
	 * @param event
	 */
	@FXML
	private void clickEvent(ActionEvent event) {

	}

}
