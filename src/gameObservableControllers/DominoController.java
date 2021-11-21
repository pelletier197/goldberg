package gameObservableControllers;

import gameObservables.Bascule;
import gameObservables.Domino;
import gameObservables.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class DominoController implements ChildrenController {

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	@FXML
	private ImageView dominoView;
	

	/**
	 * The bascule object.
	 */
	private Domino domino;

	/**
	 * Sets the view's bascule, on which properties are binded.
	 * 
	 * @param hammer
	 *            The view's Bascule
	 */
	public void setDomino(Domino hammer) {
		if (hammer == null)
			throw new NullPointerException();
		this.domino = hammer;
		initializeBinding();
	}

	/**
	 * Initializes the binding of the view's part to the {@link #domino}'s parts
	 * from properties.
	 */
	private void initializeBinding() {

		dominoView.fitWidthProperty().bind(domino.widthProperty());
		dominoView.fitHeightProperty().bind(domino.heightProperty());
		dominoView.rotateProperty().bind(domino.rotationProperty());
		dominoView.layoutXProperty().bind(domino.topLeftCornerXProperty());
		dominoView.layoutYProperty().bind(domino.topLeftCornerYProperty());


	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	protected void notifyPressed(MouseEvent event) {
		if (parent != null)
			parent.notifyChildrenPressed(domino, Observable.BASCULE, this, event);
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
