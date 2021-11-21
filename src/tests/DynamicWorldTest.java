package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import gameObservables.Spring;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import observables.AbstractComplexObservable;
import observables.DynamicWorld;
import observables.DynamicWorld.Bounds;

public class DynamicWorldTest {
	private DynamicWorld world;
	private DoubleBinding height;
	private DoubleBinding width;

	@Before
	public void before() {
		height = new SimpleDoubleProperty(500).add(0);
		width = new SimpleDoubleProperty(500).add(0);
		world = new DynamicWorld(height, width);
		passed1 = false;
	}

	@Test
	public void testDynamicWorldReadOnlyDoublePropertyReadOnlyDoublePropertyDouble() {
		// tests for null Height
		try {
			world = new DynamicWorld(null, width);
			fail();
		} catch (Exception e) {

		}
		// tests for null width
		try {
			world = new DynamicWorld(height, null);
			fail();
		} catch (Exception e) {

		}
		// tests for success
		try {
			world = new DynamicWorld(height, width);
		} catch (Exception e) {
			fail();
		}
		// tests for a success
		try {
			world = new DynamicWorld(height, width, 0);

		} catch (Exception e) {
			fail();
		}
		// tests for null Height
		try {
			world = new DynamicWorld(height, width, 10);

		} catch (Exception e) {
			fail();
		}
		// tests for null Height
		try {
			world = new DynamicWorld(height, width, -10);

		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testAddComplexObject() {
		final ObservableList<AbstractComplexObservable> observables = world.getObservables();

		assertTrue(observables.size() == 0);

		world.addComplexObject(new Spring(3, 3, 3));
		assertTrue(observables.size() == 1);
		try {
			// the list should be unmodifiable
			observables.add(new Spring(2, 2, 2));
			fail();
		} catch (Exception e) {

		}
		// Assert that the list is unmodifiable
		assertTrue(observables.size() == 1);

	}

	@Test
	public void testAddAllComplexObjects() {

		world.addAllComplexObjects(new Spring(3, 3, 3), new Spring(2, 2, 2));
		assertTrue(world.getObservables().size() == 2);
	}

	@Test
	public void testIsBounded() {
		// Not bounded at construction
		assertFalse(world.isBounded());

		world.addBounds(Bounds.BOTTOM);
		assertTrue(world.isBounded());

		world.clearBounds();
		assertFalse(world.isBounded());
	}

	@Test
	public void testSetAllBound() {

		world.setAllBound(Bounds.BOTTOM, Bounds.LEFT, Bounds.RIGHT);

		List<Bounds> bounds = Arrays.asList(world.getBounds());

		assertTrue(bounds.contains(Bounds.BOTTOM) && bounds.contains(Bounds.LEFT) && bounds.contains(Bounds.RIGHT));
		assertFalse(bounds.contains(Bounds.TOP));
	}

	@Test
	public void testClearBounds() {

		world.setAllBound(Bounds.BOTTOM, Bounds.LEFT, Bounds.RIGHT);
		assertTrue(world.isBounded());

		world.clearBounds();

		assertFalse(world.isBounded());
	}

	@Test
	public void testBoundsCrossedTeleportation() {
		world.setAllBound(Bounds.BOTTOM, Bounds.LEFT, Bounds.RIGHT);
		assertTrue(world.isBounded());

		// No change should be performed
		world.setBoundsCrossedTeleportation(false);
		assertFalse(world.isBoundsCrossedTeleported());
		assertTrue(world.isBounded());

		world.setBoundsCrossedTeleportation(true);
		assertTrue(world.isBoundsCrossedTeleported());
		assertFalse(world.isBounded());

	}

	@Test
	public void testSetGravity() {
		world.setGravity(10);
		assertTrue(world.getGravity() == 10);

		world.setGravity(-10);
		assertTrue(world.getGravity() == -10);
	}

	private boolean passed1;
	private boolean passed2;

	@Test
	public void testSetDynamic() {

		world.setDynamic(true);

		Spring s1 = new Spring(2, 3, 3);
		Spring s2 = new Spring(2, 2, 3);

		// should pass
		s1.setOnDynamicCollide((point, object, body, world) -> {
			passed1 = true;
		});
		// Should not pass
		s1.setOnStaticCollide((point, object, body, world) -> {
			passed2 = true;
		});

		world.addAllComplexObjects(s1, s2);
		passed1 = false;
		passed1 = false;

		// Simulate a collision between 2 springs from the world
		world.begin(new ContactPoint(null, s1.getBodies().get(0), s1.getBodies().get(0).getFixture(0),
				s2.getBodies().get(0), s2.getBodies().get(0).getFixture(0), new Vector2(), new Vector2(), 3));

		// Assert the collision between 2 springs has been handled correctly
		assertTrue(passed1);
		assertFalse(passed2);

	}

	@Test
	public void testSetStatic() {
		world.setDynamic(false);

		Spring s1 = new Spring(2, 3, 3);
		Spring s2 = new Spring(2, 2, 3);

		// should not pass
		s1.setOnDynamicCollide((point, object, body, world) -> {
			passed1 = true;
		});
		// Should pass
		s1.setOnStaticCollide((point, object, body, world) -> {
			passed2 = true;
		});

		world.addAllComplexObjects(s1, s2);
		passed1 = false;
		passed1 = false;

		// Simulate a collision between 2 springs from the world
		world.begin(new ContactPoint(null, s1.getBodies().get(0), s1.getBodies().get(0).getFixture(0),
				s2.getBodies().get(0), s2.getBodies().get(0).getFixture(0), new Vector2(), new Vector2(), 3));

		// Assert the collision between 2 springs has been handled correctly
		assertFalse(passed1);
		assertTrue(passed2);
	}

}
