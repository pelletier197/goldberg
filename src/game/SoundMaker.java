package game;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * Javafx based sound maker for collision sounds and game music (for different
 * interfaces).
 * 
 * Call {@link #playSound(GameSound)} to play a sound during a collision between
 * 2 objects, and call it with the appropriate parameter.
 * 
 * Call {@link #playSong(GameSong)} to play the background song associated to
 * the parameter. Note that only one {@link GameSong} can be played at once, but
 * as much {@link GameSound} as desired can be played at the same time.
 * 
 * @author Sunny, Etienne
 *
 */
public class SoundMaker {

	/**
	 * The map that holds the MediaPlayers. The key is a constant from
	 * {@link GameSong}, and the value is a List of {@link MediaPlayer}. Instead
	 * of creating a new MediaPlayer every time, we reuse the same as much as
	 * desired for the same sound.
	 */
	private static Map<GameSound, List<MediaPlayer>> gameSounds = new HashMap<>();

	/**
	 * Play the sound sent in parameter via a JavaFx {@link MediaPlayer}. Note
	 * that many sounds can be played at the same time.
	 * 
	 * @param sound
	 *            The sound to be played.
	 */
	public static void playSound(GameSound sound) {
		if (sound != null) {

			// Creates a new list if this sound has never been played before
			if (gameSounds.get(sound) == null) {
				gameSounds.put(sound, new ArrayList<MediaPlayer>());
			}

			final List<MediaPlayer> media = gameSounds.get(sound);
			final ListIterator<MediaPlayer> iterator = media.listIterator();
			MediaPlayer actu = null;
			boolean performed = false;

			// Iterates the list to find a MediaPlayer that is not playing
			while (iterator.hasNext()) {

				actu = iterator.next();

				if (actu.getStatus() != Status.PLAYING) {
					actu.play();
					performed = true;
					break;
				}
			}
			// If no media player is playing, then we create a new one.
			if (!performed) {

				Media song = null;
				try {
					song = new Media(sound.getURL().toURI().toString());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				MediaPlayer player = new MediaPlayer(song);

				media.add(player);

				// Play the sound
				player.play();

			}
		}
	}

	/**
	 * The currently playing song in the game.
	 */
	private static GameSong currentPlaying;

	/**
	 * The MediaPlayer that plays the song in the game. Only
	 * {@link #playSong(GameSong)} modifies this parameter.
	 */
	private static MediaPlayer songPlayer = null;

	/**
	 * Plays the song sent in parameter. The song will be played indefinitely.
	 * 
	 * @param song
	 *            The song to be played. If null, the song player is simply
	 *            stopped.
	 */
	public static void playSong(GameSong song) {
		if (song != currentPlaying) {

			// If the song was already playing or just exist, we release
			// resources and stop it.
			if (songPlayer != null) {
				songPlayer.stop();
				songPlayer.dispose();
			}

			// Plays the new song.
			if (song != null) {
				try {
					songPlayer = new MediaPlayer(new Media(song.getURL().toURI().toString()));

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				songPlayer.play();
				songPlayer.setCycleCount(MediaPlayer.INDEFINITE);

			}
		}
		currentPlaying = song;
	}
}
