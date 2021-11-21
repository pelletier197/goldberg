/**
 * 
 */
package tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import game.BorderType;
import game.GoldbergGame;
import game.Inventory;
import game.InventoryItem;
import game.Level;
import game.Planet;
import gameObservableViews.ObservableWrapper;
import gameObservables.Bascule;
import gameObservables.Coin;
import gameObservables.Observable;
import gameObservables.PotOfGold;

/**
 * @author 1431740
 *
 */
public class GoldbergGameTest {

	private GoldbergGame GG1;
	private Level test;
	private InventoryItem item1, item2;
	private Inventory inventory;
	private ObservableWrapper wrapper;
	private Coin coin;

	/**
	 * 
	 */
	@Before
	public void CreateInformation() {
		GG1 = new GoldbergGame();
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
		coin = new Coin(4);
		test.addFixedObject(coin);
		test.addFixedObject(new PotOfGold(3, 3));
		test.setInventory(inventory);
		GG1.setLevel(test);

		wrapper = new ObservableWrapper();
		wrapper.observable = new Bascule(7, 8);
		wrapper.instance = Observable.BASCULE;
		

	}

	/**
	 * Test method for {@link game.GoldbergGame#getAllowedInventory()}.
	 */
	@Test
	public void testGetAllowedInventory() {
		assertTrue(GG1.getAllowedInventory().getItem(Observable.ROPE).getQuantity() == 10);
	}

	/**
	 * Test method for {@link game.GoldbergGame#canBuild()}.
	 */
	@Test
	public void testCanBuilt() {
		assertTrue(GG1.canBuild());
	}

	/**
	 * Test method for
	 * {@link game.GoldbergGame#addGameObject(gameObservableViews.ObservableWrapper)}
	 * .
	 */
	@Test
	public void testAddGameObject() {
		assertTrue(GG1.getStatus().equals(GoldbergGame.Status.PREPARING));
		GG1.addGameObject(wrapper);
		assertTrue(GG1.getWrappers().get(0).equals(wrapper));
	}


	/**
	 * Test method for
	 * {@link game.GoldbergGame#setStatus(game.GoldbergGame.Status)}.
	 */
	@Test
	public void testSetStatus() {
		assertTrue(GG1.getStatus().equals(GoldbergGame.Status.PREPARING));
		GG1.setStatus(GoldbergGame.Status.BUILDING);
		assertTrue(GG1.getStatus().equals(GoldbergGame.Status.BUILDING));
	}

}
