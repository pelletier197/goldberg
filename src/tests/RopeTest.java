package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.junit.Test;

import gameObservables.Coin;
import gameObservables.Rope;

public class RopeTest {

	Rope r = new Rope(4);

	@Test
	public void testHandleDynamicCollide() {
		r.translate(2, 2);
		World w = new World();
		r.initializeObject(w);
		Coin c = new Coin(2);
		r.handleDynamicCollide(null, c, r.getBodies().get(1), w);
		// Assert that a second joint is created
		assertTrue(w.getJoints().get(1).isActive());

	}

	@Test
	public void testInitializeObject() {
		World w = new World();
		r.initializeObject(w);

		assertFalse(r.getJoints().isEmpty());
		assertTrue(r.getJoints().get(0).isActive());
	}


	@Test
	public void testTranslate() {
		r.translate(4, 4);
		assertTrue(r.getTranslate().x == 4);
		assertTrue(r.getTranslate().y == 4);

		r.translate(6, 6);
		assertTrue(r.getTranslate().x == 6);
		assertTrue(r.getTranslate().y == 6);

		r.translate(-4, -4);
		assertTrue(r.getTranslate().x == -4);
		assertTrue(r.getTranslate().y == -4);

		r.translate(6, -6);
		assertTrue(r.getTranslate().x == 6);
		assertTrue(r.getTranslate().y == -6);
	}


	@SuppressWarnings("deprecation")
	@Test
	public void testDropCoin() {
		
		r.translate(2, 2);
		World w = new World();
		r.initializeObject(w);
		Coin c = new Coin(2);

		assertTrue(r.canCatch());

		r.handleDynamicCollide(null, c, r.getBottomRopeBody(), w);
		
		Joint j = r.getJoints().get(1);

		assertTrue(w.getJoints().contains(j));
		assertTrue(r.getJoints().contains(j));
		assertFalse(r.canCatch());
		assertFalse(r.getGrippedCoin() == null);

		r.dropCoin();
		assertFalse(w.getJoints().contains(j));
		assertFalse(r.getJoints().contains(j));
		assertFalse(r.canCatch());
		assertTrue(r.getGrippedCoin() == null);

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(r.canCatch());
		assertTrue(r.getGrippedCoin() == null);

	}

	@Test
	public void testOperateResize() {
		// Visual tests only

	}

}
