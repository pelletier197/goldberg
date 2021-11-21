package views.screenController;

import java.net.URL;

/**
 * An enum for the possible themes that can be used in the application.
 * 
 * @author sunny
 *
 */
public enum Themes {

	// Only part that is being modified
	DARK("/style/DarkTheme.css");

	private String themeReferencePath;

	/**
	 * The constructor of the theme.
	 * 
	 * @param themePath
	 */
	private Themes(String themePath) {

		themeReferencePath = themePath;
	}

	/**
	 * @return Returns an URL corresponding to the theme's position on the disk.
	 */
	public URL getThemePath() {

		return getClass().getResource(themeReferencePath);
	}

	/**
	 * Returns the theme as a string, which is the path reference given in the
	 * enum's parameter.
	 */
	public String toString() {
		return themeReferencePath;
	}
}
