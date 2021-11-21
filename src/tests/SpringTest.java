package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import gameObservables.Spring;

public class SpringTest {

	Spring s = new Spring(5, 10, 4);



	@Test
	public void testRotate() {
		s.rotate(2);

		assertTrue(s.getRotate() == 2);

	}


	@Test
	public void testInitializeObject() {
		World w = new World();
		s.initializeObject(w);
		assertTrue(w.containsJoint(s.getLinearJoint()));
		assertTrue(w.containsJoint(s.getSpringEffect()));
	}

	@Test
	public void testTranslate() {

		s.translate(3, 3);
		Vector2 translatePos = new Vector2(3, 3);
		assertTrue(s.getBaseBody().getWorldCenter().x == translatePos.x);
		assertTrue(s.getBaseBody().getWorldCenter().y == translatePos.y);

		s.translate(7, 9);
		Vector2 translatePos1 = new Vector2(7, 9);
		assertTrue(s.getBaseBody().getWorldCenter().x == translatePos1.x);
		assertTrue(s.getBaseBody().getWorldCenter().y == translatePos1.y);

	}



	@Test
	public void testSetJointProperties() {

		World w = new World();

		s.initializeObject(w);
		s.setSpringConstant(0);

		assertTrue(s.getSpringEffect().getDampingRatio() <= 0.00001);
		assertTrue(s.getSpringEffect().getFrequency()<=0.0001);

		s.setSpringConstant(5);

		assertTrue(s.getSpringEffect().getDampingRatio() == 0.05);
		assertTrue(s.getSpringEffect().getFrequency() == 1.25);

		s.setSpringConstant(1000000);

		assertTrue(s.getSpringEffect().getDampingRatio() == 1);
		assertTrue(s.getSpringEffect().getFrequency() == 250000);
		
		s.unlock();

		assertFalse(s.getSpringEffect().getDistance() == 0);

		assertTrue(s.getSpringEffect().getDistance() == 3.2);

	}

}
