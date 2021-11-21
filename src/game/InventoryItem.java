package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import gameObservables.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class InventoryItem implements Serializable {

	/**
	 * The serial version UID of the object
	 */
	private static final long serialVersionUID = -4825991448936618027L;

	/**
	 * The type of this item. This parameter tells what observable is contained
	 * in this item.
	 */
	private Observable itemType;

	/**
	 * The quantity type associated to the inventory item. Can either be
	 * countable or infinite.
	 */
	private Quantity quantityType;

	/**
	 * The Integer value of the quantity. Is always considered >= 0
	 */
	private int quantity;

	/**
	 * The integer property associated to the level. This property is not
	 * serialized, and is created again in
	 * {@link #readObject(ObjectInputStream)} method.
	 */
	private transient IntegerProperty pQuantity;

	/**
	 * Default constructor of the inventory item. Creates the inventory item
	 * associated to the parameter, and gives it a quantity of 0, set as
	 * countable.
	 * 
	 * @param itemType
	 *            The item type of the item
	 */
	public InventoryItem(Observable itemType) {
		this(itemType, 0);
	}

	/**
	 * Creates the inventory item of the specified type at the specified
	 * quantity.
	 * 
	 * @param itemType
	 *            The item type of the object.
	 * @param quantity
	 *            The quantity of the object that is available.
	 */
	public InventoryItem(Observable itemType, int quantity) {
		if (itemType == null) {
			throw new NullPointerException("itemType cannot be null");
		}

		this.itemType = itemType;
		this.quantityType = Quantity.COUNTABLE;
		this.pQuantity = new SimpleIntegerProperty();

		setQuantity(quantity);
	}

	/**
	 * Creates the inventory item associated to the quantity. If the quantity is
	 * COUNTABLE, the item is set to 0 quantity of the object, or to
	 * Integer.MAX_VALUE if the quantity is INFINITE.
	 * 
	 * @param itemType
	 *            The item type of the inventory item.
	 * 
	 * @param quantity
	 *            The quantity type of the item.
	 */
	public InventoryItem(Observable itemType, Quantity quantity) {
		this(itemType, 0);
		setQuantityType(quantity);
	}

	/**
	 * Decrement the quantity of the item, only if the future quantity is going
	 * to be higher or equal to zero, and if the number of items are countable.
	 * 
	 * In any other case, no operation is performed
	 */
	public boolean decrement() {
		if (getQuantity() >= 1 && quantityType == Quantity.COUNTABLE) {
			setQuantity(getQuantity() - 1);
			return true;
		} else if (quantityType == Quantity.INFINITE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Called when object is read. Used to create the IntegerProperty
	 * 
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		this.pQuantity = new SimpleIntegerProperty(quantity);

	}

	/**
	 * Increment the quantity of the item, if the actual object is countable,
	 * however, no operation is performed.
	 */
	public void increment() {
		if (quantityType == Quantity.COUNTABLE)
			setQuantity(getQuantity() + 1);
	}

	/**
	 * Sets the quantity of the object to the specified value
	 * 
	 * @param quantity
	 *            The quantity to set the item.
	 * 
	 * @throws IllegalArgumentException
	 *             If quantity < 0
	 */
	public void setQuantity(int quantity) {
		if (quantity < 0)
			throw new IllegalArgumentException("quantity must be >= 0");

		this.pQuantity.set(quantity);
		this.quantity = quantity;
		this.quantityType = Quantity.COUNTABLE;
	}

	/**
	 * Sets the quantityType. If the type parameter is COUNTABLE, the value
	 * returned by {@link #getQuantity()} is not changed. However, if the type
	 * is INFINITE, the value returned by {@link #getQuantity()} will be equal
	 * to Integer.MAX_VALUE.
	 * 
	 * @param type
	 *            The new quantity type of the object.
	 */
	public void setQuantityType(Quantity type) {
		if (type == null) {
			throw new NullPointerException("type cannot be null");
		}
		this.quantityType = type;
		if (type == Quantity.INFINITE) {
			this.pQuantity.set(Integer.MAX_VALUE);
		}
	}

	/**
	 * 
	 * @return A read only double property expressing the quantity of the item.
	 *         Will be equal to Integer.MAX_VALUE if {@link #getQuantityType()}
	 *         == INFINITE.
	 */
	public ReadOnlyIntegerProperty quantityProperty() {
		return pQuantity;
	}

	/**
	 * Returns the quantity of the item. This method should only be called as a
	 * readable value. To pick 1 quantity of this item, call
	 * {@link #decrement()} method.
	 * 
	 * @return A {@link ReadOnlyDoubleProperty} of the quantity.
	 */
	public int getQuantity() {
		return quantityType == Quantity.INFINITE ? Integer.MAX_VALUE : pQuantity.get();
	}

	/**
	 * @return The item type of the inventoryItem
	 * 
	 */
	public Observable getItemType() {
		return itemType;
	}

	/**
	 * 
	 * @return The quantity type of the item.
	 */
	public Quantity getQuantityType() {
		return quantityType;
	}
}
