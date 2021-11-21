package interfaceViewControllers;

import game.InventoryItem;
import game.Quantity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Simple cell that contains the inventory item informations
 * 
 * @author Sunny. Mathieu
 *
 */
public class ItemBoxController {

	@FXML
	private Label name;

	@FXML
	private ImageView image;

	/**
	 * The item displayed in the view
	 */
	private InventoryItem item;

	/**
	 * Sets the current item of the view. The quantity property is binded to
	 * ensure that value is modified.
	 * 
	 * @param item
	 *            The item to be set
	 */
	public void setItem(InventoryItem item) {
		this.item = item;
		image.setImage(new Image(item.getItemType().getItemPicture()));

		setText();

		// Listen to the change of the quantity
		this.item.quantityProperty().addListener((value, old, newv) -> {
			setText();
		});
	}

	/**
	 * Set the text of the item matching the values of quantity and name.
	 */
	private void setText() {
		String text = item.getItemType().getName() + " (";

		if (item.getQuantityType() == Quantity.INFINITE) {
			// infinite symbol
			text += "\u221E";

		} else {
			text += item.getQuantity();
		}
		name.setText(text + ")");
	}
}
