package gameObservableControllers;

import gameObservables.Observable;
import gameObservables.Rope;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RopeController implements ChildrenController {
	@FXML
	private ImageView magnetView;

	@FXML
	private ImageView ropeView;
	@FXML
	private ImageView baseView;

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	/**
	 * The Rope object.
	 */
	private Rope rope;

	/**
	 * Notifies the rope to drop the coin that is actually grabbed.
	 * 
	 * @param event
	 */
	@FXML
	private void dropCoin(MouseEvent event) {
		rope.dropCoin();
	}

	/**
	 * Sets the view's {@link Rope}, on which properties are binded.
	 * 
	 * @param rope
	 *            The view's {@link Rope}
	 */
	public void setRope(Rope rope) {
		if (rope == null) {
			throw new NullPointerException();
		}
		this.rope = rope;
		initBinding();
	}

	/**
	 * Initializes the binding of the view's part to the {@link #bascule}'s
	 * parts from properties.
	 */
	private void initBinding() {

		baseView.fitWidthProperty().bind(rope.topWidthProperty());
		baseView.fitHeightProperty().bind(rope.topHeightProperty());
		baseView.layoutXProperty().bind(rope.topTopLeftCornerXProperty());
		baseView.layoutYProperty().bind(rope.topTopLeftCornerYProperty());
		baseView.rotateProperty().bind(rope.topRotationProperty());

		ropeView.fitWidthProperty().bind(rope.ropeWidthProperty());
		ropeView.fitHeightProperty().bind(rope.ropeHeightProperty());
		ropeView.layoutXProperty().bind(rope.ropeTopLeftCornerXProperty());
		ropeView.layoutYProperty().bind(rope.ropeTopLeftCornerYProperty());
		ropeView.rotateProperty().bind(rope.ropeRotationProperty());

		magnetView.fitWidthProperty().bind(rope.bottomWidthProperty());
		magnetView.fitHeightProperty().bind(rope.bottomHeightProperty());
		magnetView.layoutXProperty().bind(rope.bottomTopLeftCornerXProperty());
		magnetView.layoutYProperty().bind(rope.bottomTopLeftCornerYProperty());
		magnetView.rotateProperty().bind(rope.bottomRotationProperty());

	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyPressed(MouseEvent event) {
		if (parent != null) {
			parent.notifyChildrenPressed(rope, Observable.ROPE, this, event);
		}
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
