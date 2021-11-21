package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import gameObservables.Coin;

public class CoinTest {
	private Coin coin;

	@Test
	public void testCoinDouble() {
		// should success
		try {
			coin = new Coin(10);
		} catch (Exception e) {
			fail();
		}
		// should fail
		try {
			coin = new Coin(-0.00001);
			fail();
		} catch (Exception e) {

		}
		// should fail
		try {
			coin = new Coin(0);
			fail();
		} catch (Exception e) {

		}
		// should success
		try {
			coin = new Coin(10, 20, 20);
		} catch (Exception e) {
			fail();
		}
		// Tests negative position x
		try {
			coin = new Coin(10, -0.001, 20);
		} catch (Exception e) {
			fail();
		}
		// Tests negative position y
		try {
			coin = new Coin(10, 20, -0.0001);
		} catch (Exception e) {
			fail();
		}
		// Tests negative radius
		try {
			coin = new Coin(-0.001, 20, 20);
			fail();
		} catch (Exception e) {

		}
		// Tests negative position x and angle tetha
		try {
			coin = new Coin(10, -0.001, 20, 20);

		} catch (Exception e) {
			fail();
		}
		// Should success, negative x and -tetha
		try {
			coin = new Coin(10, -0.001, 20, -20);

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testSetMass() {
		coin = new Coin(1);
		coin.setMass(2);

		assertTrue(coin.getMass() == 2);

		coin.setMass(600);
		assertTrue(coin.getMass() == 600);

	}

	@Test
	public void testStopMovement() {
		coin = new Coin(2);

		coin.setMass(3);
		assertTrue(coin.getMass() == 3);

		// The coin must not move anymore
		coin.stopMovement();
		assertTrue(coin.getMass() >= Double.MAX_VALUE);
	}

	@Test
	public void testRotate() {
		coin = new Coin(2);
		coin.translate(3, 3);
		assertTrue(coin.getTranslate().x == 3 && coin.getTranslate().y == 3);

		// The translate must not be added to the actual translation
		coin.translate(7, 4);
		assertTrue(coin.getTranslate().x == 7 && coin.getTranslate().y == 4);

	}

	@Test
	public void testTranslate() {
		coin = new Coin(2);
		// rotation is treated in the library. We give it a little chance.
		coin.rotate(20);
		assertTrue(coin.getRotate() - 20 < 0.00001);

		// The translate must not be added to the actual translation.
		// Negative rotation is treated to positive rotation.
		coin.rotate(-Math.PI * 2);
		assertTrue((Math.abs(coin.getRotate()) < 0.001));
	}

}
