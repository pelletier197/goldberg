package gameObservableControllers;

import java.net.URL;
import java.util.ResourceBundle;

import gameObservables.Observable;
import gameObservables.PotOfGold;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PotOfGoldController implements Initializable, ChildrenController {

	@FXML
	private ImageView potOfGoldView;

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	/**
	 * The PotOfGold object.
	 */
	private PotOfGold pot;

	/**
	 * Initialized the pot of gold
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		potOfGoldView.setPreserveRatio(false);

	}

	/**
	 * Sets the view's {@link PotOfGold}, on which properties are binded.
	 * 
	 * @param observable
	 *            The view's {@link PotOfGold}
	 */
	public void setPotOfGold(PotOfGold observable) {
		if (observable == null)
			throw new NullPointerException();
		pot = observable;
		initializeBinding();
	}
	
	/**
	 * Initializes the binding of the view's part to the {@link #bascule}'s
	 * parts from properties.
	 */
	private void initializeBinding() {
		// Initialize the potOfGoldView binding
		potOfGoldView.fitWidthProperty().bind(pot.widthProperty());
		potOfGoldView.fitHeightProperty().bind(pot.heightProperty());
		potOfGoldView.rotateProperty().bind(pot.rotationProperty().add(180));
		potOfGoldView.layoutXProperty().bind(pot.topLeftCornerXProperty());
		potOfGoldView.layoutYProperty().bind(pot.topLeftCornerYProperty());

	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyPressed(MouseEvent event) {
		if (parent != null)
			parent.notifyChildrenPressed(pot, Observable.POT_OF_GOLD, this, event);
	}

	/**
	 * Starts a full drag event on the view. The parent will be able to detect
	 * drag event.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyDragged(MouseEvent event) {
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
