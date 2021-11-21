/**
 * 
 */
package tests;

import static org.junit.Assert.*;
import game.InventoryItem;
import game.Quantity;
import gameObservables.Observable;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Mathieu
 *
 */
public class InventoryItemTest {

	private InventoryItem item1, item2;
	
	@Before
	public void createInfo(){
		item1 = new InventoryItem(Observable.POT_OF_GOLD, 3);
	}
	/**
	 * Test method for {@link game.InventoryItem#InventoryItem(gameObservables.Observable)}.
	 */
	@Test
	public void testInventoryItemObservable() {
		item2 = new InventoryItem(Observable.BASCULE);
		assertTrue(item2.getQuantity() == 0);
		assertTrue(item2.getItemType().equals(Observable.BASCULE));
	}

	/**
	 * Test method for {@link game.InventoryItem#InventoryItem(gameObservables.Observable, int)}.
	 */
	@Test
	public void testInventoryItemObservableInt() {
		item2 = new InventoryItem(Observable.BASCULE, 9);
		assertTrue(item2.getQuantity() == 9);
		assertTrue(item2.getItemType().equals(Observable.BASCULE));
	}

	/**
	 * Test method for {@link game.InventoryItem#InventoryItem(gameObservables.Observable, game.Quantity)}.
	 */
	@Test
	public void testInventoryItemObservableQuantity() {
		item2 = new InventoryItem(Observable.BASCULE, Quantity.COUNTABLE);
		assertTrue(item2.getQuantityType().equals(Quantity.COUNTABLE));
		assertTrue(item2.getItemType().equals(Observable.BASCULE));
	}

	/**
	 * Test method for {@link game.InventoryItem#decrement()}.
	 */
	@Test
	public void testDecrement() {
		assertTrue(item1.getQuantity() == 3);
		item1.decrement();
		item1.decrement();
		assertTrue(item1.getQuantity() == 1);
		assertTrue(item1.decrement());
		assertFalse(item1.decrement());
	}

	/**
	 * Test method for {@link game.InventoryItem#increment()}.
	 */
	@Test
	public void testIncrement() {
		assertTrue(item1.getQuantity() == 3);
		item1.increment();
		item1.increment();
		assertTrue(item1.getQuantity() == 5);

	}

	/**
	 * Test method for {@link game.InventoryItem#setQuantity(int)}.
	 */
	@Test
	public void testSetQuantity() {
		boolean verify = false;
		try{
			item1.setQuantity(-5);
		}catch(IllegalArgumentException e){
			verify = true;
		}
		assertTrue(verify);
		item1.setQuantity(3);
		assertTrue(item1.getQuantity()==3);
		item2 = new InventoryItem(Observable.BASCULE, Quantity.INFINITE);
		item2.setQuantity(10);
		assertTrue(item2.getQuantity()==10);
		assertTrue(item2.getQuantityType().equals(Quantity.COUNTABLE));
	}

	/**
	 * Test method for {@link game.InventoryItem#setQuantityType(game.Quantity)}.
	 */
	@Test
	public void testSetQuantityType() {
		boolean verify = false;
		try{
			item1.setQuantityType(null);;
		}catch(NullPointerException e){
			verify = true;
		}
		assertTrue(verify);
		item1.setQuantityType(Quantity.INFINITE);
		assertTrue(item1.getQuantityType().equals(Quantity.INFINITE));
	}

	/**
	 * Test method for {@link game.InventoryItem#getQuantity()}.
	 */
	@Test
	public void testGetQuantity() {
		assertTrue(item1.getQuantity() == 3);
		item1.setQuantityType(Quantity.INFINITE);
		assertTrue(item1.getQuantity() == Integer.MAX_VALUE);
	}

}
