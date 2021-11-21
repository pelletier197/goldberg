package interfaceViewControllers;

import java.net.URISyntaxException;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;

/**
 * The controller that controls the view shown to the user when it win or lost
 * the game.
 * 
 * @author Etienne, Sunny
 *
 */
public class EndGameController {
	/**
	 * The video media
	 */
	@FXML
	private MediaView video;

	/**
	 * The sound media
	 */
	private MediaPlayer sound;

	/**
	 * Possible states for the game, either WIN or LOST
	 *
	 * @author Etienne
	 *
	 */
	public enum State {
		WIN("/video/win_video.mp4", "/audio/win_sound.mp3"), LOST("/video/lost_video.mp4", "/audio/lost_sound.mp3");

		private String videoPath;
		private String soundPath;

		State(String videoPath, String soundPath) {
			this.videoPath = videoPath;
			this.soundPath = soundPath;
		}
	}

	/**
	 * Sets the state of the controller. Will load the video in the medias.
	 * 
	 * @param state
	 *            The state to which set the view.
	 */
	public void setState(State state) {
		if (state != null) {
			try {
				video.setMediaPlayer(
						new MediaPlayer(new Media(getClass().getResource(state.videoPath).toURI().toString())));

				sound = new MediaPlayer(new Media(getClass().getResource(state.soundPath).toURI().toString()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Starts the media to play. This method must absolutely be called, or
	 * nothing is going to happen in the view.
	 */
	public void start() {

		video.getMediaPlayer().setAutoPlay(true);
		video.getMediaPlayer().setCycleCount(MediaPlayer.INDEFINITE);

		video.getMediaPlayer().play();
		sound.play();
	}

	/**
	 * Stops the media of playing. Must be called before the view is removed
	 * from the view.
	 */
	public void stop() {
		video.getMediaPlayer().stop();
		if (sound.getStatus() == Status.PLAYING) {
			sound.stop();
		}
	}

}
