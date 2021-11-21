package gameObservableControllers;

import java.net.URL;
import java.util.ResourceBundle;

import gameObservables.Observable;
import gameObservables.StickWall;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class StickWallController implements Initializable, ChildrenController {

	@FXML
	private ImageView stickWallView;

	/**
	 * The Stickwall object.
	 */
	private StickWall stickWall;

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	/**
	 * Initializes the stickwall's view
	 */
	@Override
	public void initialize(URL location, ResourceBundle ressources) {
		stickWallView.setPreserveRatio(false);

	}

	/**
	 * Initializes the binding of the view's part to the {@link #bascule}'s
	 * parts from properties.
	 */
	private void initializeBinding() {

		// Initialize the stickWallView binding
		stickWallView.fitWidthProperty().bind(stickWall.widthProperty());
		stickWallView.fitHeightProperty().bind(stickWall.heightProperty());
		stickWallView.rotateProperty().bind(stickWall.rotationProperty());
		stickWallView.layoutXProperty().bind(stickWall.topLeftCornerXProperty());
		stickWallView.layoutYProperty().bind(stickWall.topLeftCornerYProperty());

	}

	/**
	 * Sets the stickwall's {@link StickWall}, on which properties are binded.
	 * 
	 * @param stickWall
	 *            The view's {@link StickWall}
	 */
	public void setStickWall(StickWall stickWall) {
		this.stickWall = stickWall;
		initializeBinding();
	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyPressed(MouseEvent event) {
		if (parent != null)
			parent.notifyChildrenPressed(stickWall, Observable.STICKWALL, this, event);
	}

	/**
	 * Starts a full drag event on the view. The parent will be able to detect
	 * drag event.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyDragged(MouseEvent event) {

		// Tells the UI to check for dragEvent
		((Node) event.getSource()).startFullDrag();
	}

	/**
	 * Sets the parent controller.
	 */
	@Override
	public void setParentController(ParentController parent) {
		this.parent = parent;

	}

}
