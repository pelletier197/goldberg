package interfaceViewControllers;

import java.awt.Dialog;
import java.io.IOException;

import javax.activation.UnsupportedDataTypeException;

import interfaceViewControllers.EndGameController.State;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialogs {

	/**
	 * Button type used by {@link #showEndDialog(State)} to give user's choice.
	 * This button gives the chance to the user to try the level again again.
	 */
	public static final ButtonType TRY_AGAIN = new ButtonType("Réessayer");

	/**
	 * Button type used by {@link #showEndDialog(State)} to give user's choice.
	 * This button should be used to get back to the main menu.
	 */
	public static final ButtonType MAIN_MENU = new ButtonType("Menu Principal");

	/**
	 * Button type used by {@link #showEndDialog(State)} to give user's choice.
	 * This button should be used to get back to the level choice.
	 */
	public static final ButtonType LEVEL_CHOICE = new ButtonType("Choix de Niveaux");

	/**
	 * Shows a dialog matching the game's visuals with the header, the content
	 * text and the alertType desired.
	 * 
	 * If the alertType doesn't match the selection of ERROR, CONFIRMATION and
	 * INFORMATION then an {@link UnsupportedDataTypeException} is thrown.
	 * 
	 * @param header
	 *            The header's text
	 * @param content
	 *            The content text
	 * @param type
	 *            The alertType desired
	 * @return The buttonType selected by the user.
	 * @throws UnsupportedDataTypeException
	 *             If type doesn't match selection
	 */
	public static ButtonType showDialog(String header, String content, AlertType type)
			throws UnsupportedDataTypeException {

		Alert alert = new Alert(type);
		ImageView dialogImage = new ImageView(getAssociatedImage(type));

		// resize the alert
		dialogImage.setFitHeight(70);
		dialogImage.setFitWidth(70);

		// Sets visual preference
		alert.setGraphic(dialogImage);
		alert.getDialogPane().getStylesheets()
				.add(Dialogs.class.getResource("/styles/DarkThemeModena.css").toExternalForm());
		alert.setContentText(content);
		alert.setHeaderText(header);

		// Sets the icon of the dialog
		(((Stage) alert.getDialogPane().getScene().getWindow()).getIcons())
				.add(new Image(Dialogs.class.getResource("/images/coinSmile.gif").toExternalForm()));
		
		alert.initStyle(StageStyle.UTILITY);

		alert.showAndWait();

		return alert.getResult();
	}

	/**
	 * Shows an end dialog matching the state sent in parameter. The dialog will
	 * show a video that will indicates either the player won or lost the game.
	 * 
	 * @param state
	 *            The state of WIN or LOST
	 * @return The button selected by the user, either {@link #LEVEL_CHOICE},
	 *         {@link #MAIN_MENU} or {@link #TRY_AGAIN}
	 */
	public static ButtonType showEndDialog(State state) {

		Alert alert = null;
		try {
			// Loads the endGame view
			FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("/interfaceViews/endGame.fxml"));

			alert = new Alert(AlertType.NONE);
			alert.setHeaderText(null);

			// Sets the button matching the state
			if (state == State.LOST) {
				alert.getButtonTypes().setAll(MAIN_MENU, LEVEL_CHOICE, TRY_AGAIN);
			} else {
				alert.getButtonTypes().setAll(TRY_AGAIN, MAIN_MENU, LEVEL_CHOICE);
			}

			// Sets visual preferences
			alert.getDialogPane().setContent(loader.load());
			alert.getDialogPane().setStyle("-fx-background-color:black");
			alert.getDialogPane().getStylesheets()
					.add(Dialogs.class.getResource("/styles/DarkThemeModena.css").toExternalForm());

			(((Stage) alert.getDialogPane().getScene().getWindow()).getIcons())
					.add(new Image(Dialogs.class.getResource("/images/coinSmile.gif").toExternalForm()));

			// Notify the controller to start
			EndGameController controller = loader.getController();
			controller.setState(state);
			controller.start();
			


			alert.showAndWait();

			// Stops the dialog
			controller.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return alert.getResult();
	}

	/**
	 * 
	 * @param type
	 *            The alert type
	 * @return the image associated to the alert type sent in parameter
	 * @throws UnsupportedDataTypeException
	 *             If the alert type doesn't have an image matching it.
	 */
	private static Image getAssociatedImage(AlertType type) throws UnsupportedDataTypeException {
		Image dialogImage = null;

		switch (type) {
		case ERROR:
			dialogImage = new Image(Dialog.class.getResource("/images/coinHit.gif").toExternalForm());
			break;

		case CONFIRMATION:
			dialogImage = new Image(Dialog.class.getResource("/images/coinHeart.gif").toExternalForm());
			break;
		case INFORMATION:
			dialogImage = new Image(Dialog.class.getResource("/images/coinSmile.gif").toExternalForm());
			break;
		default:
			throw new UnsupportedDataTypeException("The alert type " + type + " is not supported");
		}
		return dialogImage;
	}

}
