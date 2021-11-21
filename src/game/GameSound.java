package game;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * A class that represents the sounds that the game can play during collisions.
 * Use the {@link #toURI()} to get the path to the song as a valid URI, OR
 * {@link #getURL()} to get the path to the song as a URL.
 * 
 * @author Etienne
 *
 */
public enum GameSound {
	METAL_TO_METAL("/audio/metal_metal.mp3"), METAL_TO_WOOD("/audio/metal_to_wood.mp3"), MAGNET(
			"/audio/magnet.mp3"), UNKNOWN(
					"/audio/unknown.mp3"), MAIN_MENU("/audio/main_menu_song.mp3"), BUILDER("/audio/builder_song.mp3");

	private URL path;

	private GameSound(String path) {
		this.path = getClass().getResource(path);
	}

	/**
	 * 
	 * @return The path to the song as a valid URL
	 */
	public URL getURL() {
		return path;
	}

	public String toURI() {
		try {
			return path.toURI().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
