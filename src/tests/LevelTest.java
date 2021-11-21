/**
 * 
 */
package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import game.BorderType;
import game.Inventory;
import game.InventoryItem;
import game.Level;
import game.Planet;
import gameObservables.Coin;
import gameObservables.Observable;
import gameObservables.PotOfGold;

/**
 * @author Mathieu
 *
 */
public class LevelTest {

	private Level test, level1;
	private InventoryItem item1, item2;
	private Inventory inventory;
	private Coin coin;
	private PotOfGold pog;

	@Before
	public void CreateInformation() {
		item1 = new InventoryItem(Observable.POT_OF_GOLD, 3);
		item2 = new InventoryItem(Observable.ROPE, 10);
		inventory = new Inventory();
		inventory.addItem(item1);
		inventory.addItem(item2);
		try {
			test = new Level("pop", "popy", "bob");
		} catch (IOException e) {
			e.printStackTrace();
		}
		test.setBorders(BorderType.NORMAL);
		test.setPlanet(Planet.MARS);
		test.setHeight(10);
		test.setWidth(10);
		pog = new PotOfGold(3, 3);
		coin = new Coin(4);
		test.addFixedObject(coin);
		test.addFixedObject(pog);
		test.setInventory(inventory);

	}

	/**
	 * Test method for
	 * {@link game.Level#Level(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testLevel() {
		try {
			level1 = new Level("", "", "");
		} catch (IOException e) {
			fail("La méthode ne fonctionne pas");
		}

		try {
			level1 = new Level("", "", "pop");
		} catch (IOException e) {
		}
		assertTrue(level1.getName().equals("Anonyme"));
		assertTrue(level1.getCreator().equals("Anonyme"));

	}

	/**
	 * Test method for {@link game.Level#setCreator(java.lang.String)}.
	 */
	@Test
	public void testSetCreator() {
		assertTrue(test.getCreator().equals("popy"));
		test.setCreator(null);
		assertTrue(test.getCreator().equals("Anonyme"));
		test.setCreator("plante");
		assertTrue(test.getCreator().equals("plante"));
	}

	/**
	 * Test method for {@link game.Level#setName(java.lang.String)}.
	 */
	@Test
	public void testSetName() {
		assertTrue(test.getName().equals("pop"));
		test.setName(null);
		assertTrue(test.getName().equals("Anonyme"));
		test.setName("plante");
		assertTrue(test.getName().equals("plante"));
	}

	/**
	 * Test method for
	 * {@link game.Level#removeFixedObject(observables.AbstractComplexObservable)}
	 */
	@Test
	public void testRemoveFixedObject() {
		assertTrue(test.getFixedObject().get(0).equals(coin));
		test.removeFixedObject(coin);
		assertTrue(test.getFixedObject().get(0).equals(pog));
	}
	

	/**
	 * Test method for {@link game.Level#setPlanet(game.Planet)}.
	 */
	@Test
	public void testSetPlanet() {
		boolean verify = false;
		try {
			test.setPlanet(null);
		} catch (NullPointerException e) {
			verify = true;
		}
		assertTrue(verify);
		try {
			test.setPlanet(Planet.JUPITER);
		} catch (NullPointerException e) {
			fail();
		}
	}

	/**
	 * Test method for {@link game.Level#compareTo(game.Level)}.
	 */
	@Test
	public void testCompareTo() {
		try {
			level1 = new Level("pop", "chanson", "path");
		} catch (IOException e) {
		}
		
		assertTrue(level1.compareTo(test)== 0);
	}

}
