package gameObservableViews;

import java.io.IOException;

import game.InventoryItem;
import interfaceViewControllers.ItemBoxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

/**
 * Simple class that provides a ListCell used to represent an inventory item is
 * a listView.
 * 
 * @author Sunny, Etienne
 *
 */
public class InventoryListCell extends ListCell<InventoryItem> {

	/**
	 * Loads the {@link ItemBoxController}'s view for the item sent in
	 * parameter.
	 */
	@Override
	protected void updateItem(InventoryItem item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null && !empty) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaceViews/itemBox.fxml"));
			try {
				setGraphic(loader.load());
			} catch (IOException e) {
				e.printStackTrace();
			}
			ItemBoxController controller = loader.getController();
			controller.setItem(item);
		} else {
			setGraphic(null);
		}
	}

}
