package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import game.Inventory;
import game.InventoryItem;
import game.Quantity;
import gameObservables.Observable;

public class InventoryTest {

	private InventoryItem item1, item2;
	private Inventory inventory, inventory2;

	@Before
	public void before() {
		item1 = new InventoryItem(Observable.POT_OF_GOLD, 3);
		item2 = new InventoryItem(Observable.ROPE, 10);
		inventory = new Inventory();
		inventory2 = new Inventory();
		inventory2.addItem(item1);
	}

	@Test
	public void testAddItem() {
		assertTrue(inventory.getItems().size() == 0);
		inventory.addItem(item1);
		inventory.addItem(item2);
		assertTrue(inventory.getItems().size() != 0);
	}

	@Test
	public void testAddAllItem() {
		List<InventoryItem> list = new ArrayList<InventoryItem>();
		list.add(item1);
		list.add(item2);
		assertTrue(inventory.getItems().size() == 0);
		inventory.addAllItem(list);
		assertTrue(inventory.getItems().size() != 0);
	}

	@Test
	public void testPickItem() {
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertFalse(inventory2.pickItem(Observable.COIN));
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertFalse(inventory2.pickItem(Observable.POT_OF_GOLD));

	}

	@Test
	public void testPut() {
		assertFalse(inventory2.pickItem(Observable.COIN));
		inventory2.put(Observable.COIN);
		assertTrue(inventory2.pickItem(Observable.COIN));
		inventory2.put(Observable.POT_OF_GOLD);
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertTrue(inventory2.pickItem(Observable.POT_OF_GOLD));
		assertFalse(inventory2.pickItem(Observable.POT_OF_GOLD));
	}

	@Test
	public void testGetItem() {
		assertTrue(inventory2.getItem(Observable.POT_OF_GOLD).getQuantity() == 3);
		assertTrue(inventory2.getItem(Observable.ROPE) == null);
	}

	@Test
	public void testRemoveItem() {
		assertTrue(inventory2.getItem(Observable.POT_OF_GOLD).getQuantity() == 3);
		inventory2.removeItem(Observable.POT_OF_GOLD);
		assertTrue(inventory2.getItem(Observable.POT_OF_GOLD) == null);
	}

	@Test
	public void testGetQuantity() {
		assertTrue(inventory2.getItem(Observable.POT_OF_GOLD).getQuantity() == 3);
	}

	@Test
	public void testGetQuantityType() {
		assertTrue(inventory2.getItem(Observable.POT_OF_GOLD).getQuantityType().equals(Quantity.COUNTABLE));
	}

	@Test
	public void testGetItems() {
		assertTrue(inventory2.getItems().size() == 1);
	}

	@Test
	public void testMakeUnmodifiable() {
		inventory2.makeUnmodifiable();
		try{
			inventory2.addItem(item2);
			fail();
		}catch(UnsupportedOperationException e){
		}
	}

}
