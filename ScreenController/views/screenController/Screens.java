package views.screenController;

import java.net.URL;

/**
 * An enum that represents the screens that can be displayed in the program. The
 * enum will allow to get an URL corresponding to the Screen's position on the
 * disk.
 * 
 * @author sunny
 *
 */
public enum Screens {

	/**
	 * From a program to another, the enum is the only part to change. The files
	 * are represented as strings, with the path /package/fxmlFileName.fxml
	 */
	MAIN_MENU("/interfaceViews/mainMenu.fxml"), CREATE_INFO("/interfaceViews/createInfo.fxml"), BUILDER(
			"/interfaceViews/builder.fxml"), CREATED_LEVELS("/interfaceViews/levelView.fxml"), 
	CHOSE_OBJECT("/interfaceViews/choseObjectView.fxml"), MAIN_GAME("/interfaceViews/MainGame.fxml");

	private URL fxmlReferencePath;

	/**
	 * The constructor of a screen, with the String representing the package and
	 * the file's Name
	 * 
	 * @param fxmlName
	 */
	private Screens(String fxmlName) {

		fxmlReferencePath = this.getClass().getResource(fxmlName);

	}

	/**
	 * Returns an URL that corresponds to the screen's position on the disk.
	 */
	public URL getFxmlUrl() {

		return fxmlReferencePath;
	}
}
