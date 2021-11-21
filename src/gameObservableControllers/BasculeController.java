package gameObservableControllers;

import gameObservables.Bascule;
import gameObservables.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class BasculeController implements ChildrenController {

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	@FXML
	private ImageView basculeView;
	@FXML
	private ImageView bearingView;

	/**
	 * The bascule object.
	 */
	private Bascule bascule;

	/**
	 * Sets the view's bascule, on which properties are binded.
	 * 
	 * @param bascule
	 *            The view's Bascule
	 */
	public void setBascule(Bascule bascule) {
		if (bascule == null)
			throw new NullPointerException();
		this.bascule = bascule;
		initializeBinding();
	}

	/**
	 * Initializes the binding of the view's part to the {@link #bascule}'s parts
	 * from properties.
	 */
	private void initializeBinding() {

		basculeView.fitWidthProperty().bind(bascule.widthProperty());
		basculeView.fitHeightProperty().bind(bascule.heightProperty());
		basculeView.rotateProperty().bind(bascule.rotationProperty());
		basculeView.layoutXProperty().bind(bascule.topLeftCornerXProperty());
		basculeView.layoutYProperty().bind(bascule.topLeftCornerYProperty());

		bearingView.layoutXProperty().bind((bascule.topLeftCornerXProperty().add(bascule.widthProperty().divide(2)))
				.subtract(bearingView.fitWidthProperty().divide(2)));
		bearingView.layoutYProperty().bind((bascule.topLeftCornerYProperty().add(bascule.heightProperty().divide(2)))
				.subtract(bearingView.fitHeightProperty().divide(2)));

		DoubleBinding binding = Bindings.createDoubleBinding(() -> {
			return Math.min(basculeView.fitHeightProperty().get(), basculeView.fitWidthProperty().get());
		} , basculeView.fitHeightProperty(), basculeView.fitWidthProperty());

		bearingView.fitHeightProperty().bind(binding);
		bearingView.fitWidthProperty().bind(binding);

	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	protected void notifyPressed(MouseEvent event) {
		if (parent != null)
			parent.notifyChildrenPressed(bascule, Observable.BASCULE, this, event);
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
