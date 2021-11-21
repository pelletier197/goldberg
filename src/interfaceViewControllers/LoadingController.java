package interfaceViewControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class LoadingController implements views.screenController.LoadingController, Initializable {

	@FXML
	private ImageView image;

	@FXML
	private Label label;

	private Timeline anim = null;

	/**
	 * Starts the animation to play.
	 */
	@Override
	public void startLoading() {

		anim.play();

	}

	/**
	 * Stops the animation to play
	 */
	@Override
	public void stopLoading() {
		anim.stop();

	}

	/**
	 * Initializes the animation's keyframes
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		anim = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(label.opacityProperty(), 1)),
				new KeyFrame(Duration.ZERO, new KeyValue(image.rotateProperty(), 0)),
				new KeyFrame(Duration.seconds(3), new KeyValue(label.opacityProperty(), 0)),
				new KeyFrame(Duration.seconds(6), new KeyValue(label.opacityProperty(), 1)),
				new KeyFrame(Duration.seconds(6), new KeyValue(label.opacityProperty(), 1)),
				new KeyFrame(Duration.seconds(6), new KeyValue(image.rotateProperty(), 360)));
		anim.setCycleCount(Timeline.INDEFINITE);

		image.setCache(true);
		image.setCacheHint(CacheHint.SPEED);

		label.setCache(true);
		label.setCacheHint(CacheHint.SPEED);

	}

}
