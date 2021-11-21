package gameObservableControllers;

import java.net.URL;
import java.util.ResourceBundle;

import gameObservables.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import observables.Surface;

public class SurfaceController implements Initializable, ChildrenController {

	/**
	 * Available views for the Surface.
	 */
	private static final Image[] VIEWS = {

			new Image(SurfaceController.class.getResource("/images/Surface1.png").toExternalForm())

	};
	@FXML
	private ImageView surfaceView;

	/**
	 * The Surface object.
	 */
	private Surface surface;

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	/**
	 * Initializes the surface's view
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		surfaceView.setPreserveRatio(false);

	}

	/**
	 * Initializes the binding of the view's part to the {@link #bascule}'s
	 * parts from properties.
	 */
	private void initializeBinding() {

		surfaceView.setImage(VIEWS[(int) (Math.random() * VIEWS.length)]);
		// Initialize the surfaceView binding
		surfaceView.fitWidthProperty().bind(surface.widthProperty());
		surfaceView.fitHeightProperty().bind(surface.heightProperty());
		surfaceView.rotateProperty().bind(surface.rotationProperty());
		surfaceView.layoutXProperty().bind(surface.topLeftCornerXProperty());
		surfaceView.layoutYProperty().bind(surface.topLeftCornerYProperty());

	}

	/**
	 * Sets the view's {@link Surface}, on which properties are binded.
	 * 
	 * @param spring
	 *            The view's {@link Surface}
	 */
	public void setSurface(Surface surface) {
		this.surface = surface;
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
			parent.notifyChildrenPressed(surface, Observable.STICKWALL, this, event);
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
