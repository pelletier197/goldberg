package game;

import java.net.URL;

/**
 * Class used to represents the Songs allowed to the game. Use the
 * {@link #getURL()} method to receive the position of the song in the
 * resources.
 * 
 * @author Etienne
 *
 */
public enum GameSong {
	MAIN_MENU("/audio/main_menu_song.mp3"), BUILDER("/audio/builder_song.mp3");

	private URL path;

	private GameSong(String path) {
		this.path = getClass().getResource(path);
	}

	/**
	 * @return returns the Path to the song as a URL
	 */
	public URL getURL() {
		return path;
	}

}
