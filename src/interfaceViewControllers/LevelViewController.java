package interfaceViewControllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.activation.UnsupportedDataTypeException;

import game.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import views.screenController.ControlledScreen;
import views.screenController.ScreenController;
import views.screenController.ScreenController.Animations;
import views.screenController.Screens;

public class LevelViewController implements ControlledScreen, Initializable {
	@FXML
	private Label title;

	@FXML
	private ListView<Level> levelView;

	private ScreenController controller;

	@FXML
	private Button play;
	@FXML
	private Button modify;
	@FXML
	private Button delete;

	/**
	 * Called by the view when the previous button is clicked
	 * 
	 * @param event
	 */
	@FXML
	private void previousClicked(ActionEvent event) {
		controller.setScreen(Screens.MAIN_MENU, Animations.TRANSLATE_RIGHT_TO_CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScreenController(ScreenController SC) {
		this.controller = SC;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayedToScreen() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removedFromScreen() {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the window's title, as it can be used to display different views.
	 * 
	 * @param title
	 *            The title of the window
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * Sets the level of the listView that it will display. The level will be
	 * sorted by alphabetic order.
	 * 
	 * @param personnalLevels
	 *            The levels displayed in the ListView.
	 * 
	 * @param showModifyAndDelete
	 *            Boolean that tells if modify and delete button must be shown
	 *            in this view or not. At least, only the play button is
	 *            necessary to the view.
	 */
	public void setLevels(List<Level> personnalLevels, boolean showModifyAndDelete) {
		ObservableList<Level> levels = FXCollections.observableArrayList(personnalLevels);
		Collections.sort(levels);

		modify.setVisible(showModifyAndDelete);
		delete.setVisible(showModifyAndDelete);

		this.levelView.setItems(levels);
	}

	/**
	 * Initializes the view to match the restriction and to define the cell
	 * factory that will be represented by {@link LevelViewController}'s view.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.play.setDisable(true);
		this.delete.setDisable(true);
		this.modify.setDisable(true);

		this.levelView.setCellFactory(new Callback<ListView<Level>, ListCell<Level>>() {

			@Override
			public ListCell<Level> call(ListView<Level> param) {
				// TODO Auto-generated method stub
				return new ListCell<Level>() {
					@Override
					protected void updateItem(Level item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null && !empty) {
							try {
								FXMLLoader loader = new FXMLLoader(
										getClass().getResource("/interfaceViews/levelInfo.fxml"));

								setGraphic(loader.load());
								LevelInfoController controller = loader.getController();
								controller.setLevel(item);
							} catch (IOException e) {

								e.printStackTrace();
							}

						} else {
							setGraphic(null);
						}
					}

				};
			}
		});

		levelView.getSelectionModel().selectedItemProperty().addListener((value, old, newv) -> {
			if (newv != null && old == null) {
				this.play.setDisable(false);
				this.delete.setDisable(false);
				this.modify.setDisable(false);
			} else if (newv == null) {
				this.play.setDisable(true);
				this.delete.setDisable(true);
				this.modify.setDisable(true);
			}
		});

	}

	/**
	 * Deletes the level that is currently selected from the file. This level
	 * will not appear anymore.
	 * 
	 * @param event
	 */
	@FXML
	private void delete(ActionEvent event) {
		Level toDelete = levelView.getSelectionModel().getSelectedItem();

		try {
			if (Dialogs.showDialog(null, "Voulez-vous vraiment supprimer le niveau : " + toDelete.getName()
					+ " crée par " + toDelete.getCreator() + " ?", AlertType.CONFIRMATION) == ButtonType.OK) {
				levelView.getItems().remove(toDelete);

				File file = new File(toDelete.getPath());
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (UnsupportedDataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Loads the builder to modify the currently selected level in the view.
	 * 
	 * @param event
	 */
	@FXML
	private void modify(ActionEvent event) {
		Level toModify = levelView.getSelectionModel().getSelectedItem();

		controller.setScreen(Screens.BUILDER, Animations.FADE_IN);

		BuilderController builderController = (BuilderController) controller.getController(Screens.BUILDER);
		builderController.setLevel(toModify);

	}

	/**
	 * Loads the {@link MainGameController}'s view to let the user play the
	 * currently selected level in the view.
	 * 
	 * @param event
	 */
	@FXML
	private void play(ActionEvent event) {
		controller.setScreen(Screens.MAIN_GAME);

		MainGameController gameController = (MainGameController) controller.getController(Screens.MAIN_GAME);

		gameController.setLevel(levelView.getSelectionModel().getSelectedItem());
	}

}
