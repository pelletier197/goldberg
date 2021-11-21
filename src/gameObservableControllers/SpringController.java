package gameObservableControllers;

import java.net.URL;
import java.util.ResourceBundle;

import gameObservables.Observable;
import gameObservables.Spring;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class SpringController implements Initializable, ChildrenController {

	@FXML
	private ImageView plateView;

	@FXML
	private ImageView springView;

	@FXML
	private ImageView bottomView;

	@FXML
	private Pane background;
	/**
	 * The Spring object.
	 */
	private Spring spring;

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	/**
	 * Initializes the spring's view
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		plateView.setPreserveRatio(false);
		springView.setPreserveRatio(false);
		bottomView.setPreserveRatio(false);
	}

	/**
	 * Sets the view's {@link Spring}, on which properties are binded.
	 * 
	 * @param spring
	 *            The view's {@link Spring}
	 */
	public void setSpring(Spring spring) {
		this.spring = spring;
		initalizeConstraints();
	}

	/**
	 * When clicked, the spring switches lock, from locked to unlocked.
	 * 
	 * @param event
	 */
	@FXML
	private void switchLock(MouseEvent event) {
		if (spring != null) {
			if (spring.isLocked()) {
				spring.unlock();
			} else {
				spring.lock();
			}
		}
	}

	/**
	 * Initializes the binding of the view's part to the {@link #bascule}'s
	 * parts from properties.
	 */
	private void initalizeConstraints() {

		// Initialize the baseView binding
		bottomView.fitWidthProperty().bind(spring.baseWidthProperty());
		bottomView.fitHeightProperty().bind(spring.baseHeightProerty());
		bottomView.rotateProperty().bind(spring.baseRotationProperty());
		bottomView.layoutXProperty().bind(spring.baseTopLeftCornerXProperty());
		bottomView.layoutYProperty().bind(spring.baseTopLeftCornerYProperty());

		// Initialize the spring view binding
		springView.fitWidthProperty().bind(spring.springLengthProperty());
		springView.fitHeightProperty().bind(spring.springHeightProperty());
		springView.rotateProperty().bind(spring.baseRotationProperty());
		springView.layoutXProperty().bind(spring.springTopLeftCornerXProperty());
		springView.layoutYProperty().bind(spring.springTopLeftCornerYProperty());

		// Initialize the plateView binding
		plateView.fitWidthProperty().bind(spring.plateWidthProperty());
		plateView.fitHeightProperty().bind(spring.plateHeightProperty());
		plateView.rotateProperty().bind(spring.baseRotationProperty());
		plateView.layoutXProperty().bind(spring.plateTopLeftCornerXProperty());
		plateView.layoutYProperty().bind(spring.plateTopLeftCornerYProperty());

		// Solves contraction of the image to zero value.
		springView.fitWidthProperty().addListener((value, old, newv) -> {
			if (Math.abs(newv.doubleValue() - 0.001) <= 0.01) {
				springView.setVisible(false);
			} else if (Math.abs(old.doubleValue() - 0.001) <= 0.01) {
				springView.setVisible(true);
			}
		});

	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyPressed(MouseEvent event) {

		parent.notifyChildrenPressed(spring, Observable.SPRING, this, event);
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
