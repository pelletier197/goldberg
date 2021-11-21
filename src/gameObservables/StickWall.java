package gameObservables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import observables.AbstractComplexObservable;
import observables.Surface;

/**
 * In the game, a stickWall will be a wall that will make the Coin stop on
 * Collide. It Possess all the properties of its parent.
 * 
 * @author sunny
 *
 */
public class StickWall extends Surface {

	/**
	 * Minimal constructor for a Stickwall. A Stickwall is, by definition, set
	 * with an infinite mass.
	 * 
	 * @param width
	 *            The width of the Stickwall in meters
	 * @param height
	 *            The height of the Stickwall in meters
	 */
	public StickWall(double width, double height) {
		super(width, height);

		// Give the fixture's properties
		fixture.setFriction(INFINITE_FRICTION);
		fixture.setRestitution(0);

		// Give the body's properties
		body.setMass(MassType.INFINITE);

		super.body.setBullet(true);
	}

	/**
	 * Handle a dynamic collide with an AbstractComplexObject. In the case that
	 * this object is a coin, the method {@link #setMobile(boolean)} is called
	 * to the coin, and its movement is set to null, to simulate a sticky wall.
	 */
	@Override
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {
		super.handleDynamicCollide(contactPoint, object, body, world);

		if (object instanceof Coin) {
			Coin coin = (Coin) object;
			coin.getBodies().get(0).setLinearVelocity(0, 0);
			coin.getBodies().get(0).setAngularVelocity(0);
			coin.getBodies().get(0).setMass(MassType.INFINITE);
		}

	}
}
