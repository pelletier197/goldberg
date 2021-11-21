package gameObservables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import observables.AbstractComplexObservable;
import observables.Surface;

public class PotOfGold extends Surface implements AbstractComplexObservable {

	/**
	 * Constructor of the pot of gold. Same as {@link Surface}
	 * 
	 * @param width
	 *            The width in meters
	 * @param height
	 *            The height in meters.
	 */
	public PotOfGold(double width, double height) {
		super(width, height);
	}

	/**
	 * Sets the mass of the coin entering in collision to infinite. Makes the
	 * coin stop, and the game should end there if all coin have reach the pot.
	 */
	@Override
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {
		super.handleDynamicCollide(contactPoint, object, body, world);

		if (object instanceof Coin) {
			Coin coin = (Coin) object;
			coin.setMobile(false);
			coin.getBodies().get(0).setMass(MassType.INFINITE);
			coin.getBodies().get(0).setLinearVelocity(new Vector2(0, 0));
			final Vector2 center = this.body.getWorldCenter();
			coin.translate(center.x, center.y);

			// desactivate the coin
			coin.getBodies().get(0).setActive(false);
		}
	}

}
