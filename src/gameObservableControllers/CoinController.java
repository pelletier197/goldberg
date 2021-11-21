package gameObservableControllers;

import gameObservables.Coin;
import gameObservables.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CoinController implements ChildrenController {

	/**
	 * The emotions allowed for the coin
	 */
	public enum CoinEmotion {
		SMILE("/images/coinSmile.gif"), CRY("/images/coinHit.gif"), LOVE("/images/coinHeart.gif");

		private String path;

		private CoinEmotion(String path) {
			this.path = path;
		}

		protected Image getImage() {
			return new Image(BasculeController.class.getClass().getResource(path).toExternalForm());
		}
	}

	@FXML
	private ImageView coinView;

	/**
	 * The coin component
	 */
	private Coin coin;

	/**
	 * The parent controller that is notified by the event called on the view.
	 */
	private ParentController parent;

	/**
	 * Sets the view's Coin, on which properties are binded.
	 * 
	 * @param observable
	 *            The view's Coin
	 */
	public void setCoin(Coin observable) {
		if (observable == null)
			throw new NullPointerException();
		this.coin = observable;

		initializeBinding();

	}

	/**
	 * Sets the current coin emotional face to the one in parameter.
	 * 
	 * @param emotion
	 *            The new coin's emotion
	 */
	public void setCoinEmotion(CoinEmotion emotion) {

		coinView.setImage(emotion.getImage());
	}

	/**
	 * Initializes the binding of the view's part to the {@link #coin}'s parts
	 * from properties.
	 */
	private void initializeBinding() {
		coinView.fitWidthProperty().bind(coin.radiusProperty().multiply(2));
		coinView.fitHeightProperty().bind(coin.radiusProperty().multiply(2));
		coinView.rotateProperty().bind(coin.rotationProperty());
		coinView.layoutXProperty().bind(coin.topLeftCornerXProperty());
		coinView.layoutYProperty().bind(coin.topLeftCornerYProperty());
	}

	/**
	 * Notifies the {@link #parent} that the view has been pressed.
	 * 
	 * @param event
	 */
	@FXML
	private void notifyPressed(MouseEvent event) {
		if (parent != null)
			parent.notifyChildrenPressed(coin, Observable.COIN, this, event);
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
