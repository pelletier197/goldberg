package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import gameObservables.Observable;

/**
 * Inventory is a conventional class that provides method to manage an inventory
 * in the game.
 * 
 * Via the method {@link #addItem(InventoryItem)}, it is possible to add a
 * specific item to the inventory, that could be accessed via the
 * {@link #getItem(Observable)}.
 * 
 * Each Observable represents an inventory key that can be used to access an
 * item from this inventory.
 * 
 * 
 * @author sunny, Etienne, Mathieu
 *
 */
public class Inventory {

	/**
	 * The inventory where the items are stocked.
	 */
	private Map<Observable, InventoryItem> items;

	/**
	 * Constructs an empty inventory.
	 */
	public Inventory() {
		this.items = new HashMap<>();
	}

	/**
	 * Add the specified item to the inventory. If the inventory already
	 * contains this item, it is simply replaced.
	 * 
	 * @param item
	 *            The inventory item to add. If null, the inventory counts it
	 *            like the item is not contained in the inventory.
	 */
	public void addItem(InventoryItem item) {
		items.put(item.getItemType(), item);
	}

	/**
	 * Add all the inventory contained in the list.
	 * 
	 * @param list
	 *            The list of item
	 */
	public void addAllItem(List<InventoryItem> list) {
		for (InventoryItem item : list) {
			addItem(item);
		}
	}

	/**
	 * Picks the item sent in parameter from the inventory and decrement its
	 * value of one if the picking is allowed. If no object of this observable's
	 * instance is contained in the inventory, false is returned.
	 * 
	 * @param object
	 *            The object to be picked
	 * @return True if the picking is allowed, false if the observable is not
	 *         contained or if the inventory does not have enough item to remove
	 *         one.
	 */
	public boolean pickItem(Observable object) {
		return items.get(object) == null ? false : items.get(object).decrement();
	}

	/**
	 * Add one instance of the object sent in parameter to the inventory.
	 * 
	 * @param object
	 *            The object to be added
	 */
	public void put(Observable object) {
		if (items.get(object) == null) {
			items.put(object, new InventoryItem(object, 1));

		} else {
			items.get(object).increment();
		}

	}

	/**
	 * Returns the inventory item associated to the item type sent in parameter.
	 * 
	 * @param itemType
	 *            The item type to which we want to get the inventory item.
	 * @return The inventory item, or null if this object is not contained in
	 *         this inventory.
	 */
	public InventoryItem getItem(Observable itemType) {
		return items.get(itemType);
	}

	/**
	 * Completely removes the item associated to the item type sent in
	 * parameter.
	 * 
	 * @param itemType
	 *            The item type to be removed.
	 */
	public void removeItem(Observable itemType) {
		items.remove(itemType);
	}

	/**
	 * Returns the quantity of the item type that is available in the inventory.
	 * 
	 * @param itemType
	 *            The item type.
	 * 
	 * @return The quantity of the item type that is available in the inventory.
	 */
	public int getQuantity(Observable itemType) {
		return items.get(itemType) == null ? 0 : items.get(itemType).getQuantity();
	}

	/**
	 * Returns the quantity type associated to the Observable sent in parameter.
	 * 
	 * @param itemType
	 *            The item type
	 * 
	 * @return The quantity type associated to the Observable sent in parameter.
	 */
	public Quantity getQuantityType(Observable itemType) {
		return items.get(itemType) == null ? Quantity.COUNTABLE : items.get(itemType).getQuantityType();
	}

	/**
	 * 
	 * @return A list containing all the inventory items contained in this
	 *         inventory. If an item has a quantity of 0, it is not added to the
	 *         list.
	 */
	public List<InventoryItem> getItems() {

		final List<InventoryItem> answer = new ArrayList<>();

		// Iterate over the map entries and collect the not null items.
		items.forEach(new BiConsumer<Observable, InventoryItem>() {

			@Override
			public void accept(Observable t, InventoryItem u) {
				if (u != null && u.getQuantity() > 0) {
					answer.add(u);
				}
			}
		});
		return answer;
	}

	/**
	 * Makes the inventory unmodifiable. If the inventory is modified via
	 * {@link Inventory#addItem(InventoryItem)}, an exception will be thrown.
	 */
	public void makeUnmodifiable() {
		this.items = Collections.unmodifiableMap(items);
	}
}
