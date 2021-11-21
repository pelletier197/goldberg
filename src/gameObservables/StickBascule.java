package gameObservables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Vector2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import observables.AbstractComplexObservable;

public class StickBascule extends Bascule implements JointApplier {

	private WeldJoint joint;

	private Coin grippedCoin;

	private BooleanProperty canCatch;

	private World world;

	/**
	 * Minimal constructor for a StickBascule.
	 * 
	 * @param width
	 *            The width of the StickBascule
	 * @param height
	 *            The height of the StickBascule
	 */
	public StickBascule(double width, double height) {
		this(width, height, 0.00001);
	}

	/**
	 * Minimal constructor for a StickBascule.
	 * 
	 * @param width
	 *            The width of the StickBascule
	 * @param height
	 *            The height of the StickBascule
	 */
	public StickBascule(double width, double height, double mass) {
		super(width, height, mass);

		canCatch = new SimpleBooleanProperty(true);
		super.body.setGravityScale(0);
	}

	/**
	 * Handle a dynamic collide with an AbstractComplexObject. In the case that
	 * this object is a coin, the method {@link #setMobile(boolean)} is called
	 * to the coin, and a joint is formed between the coin and the StickBascule
	 * to make the coin stick on it.
	 */
	@Override
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {
		super.handleDynamicCollide(contactPoint, object, body, world);

		if (object instanceof Coin) {
			Coin coin = (Coin) object;

			// The coin must not have an infinite mass (means it would be
			// stopped)
			if (joint == null && canCatch.get() && coin.getBodies().get(0).isDynamic()) {

				this.grippedCoin = coin;

				Body b = coin.getBodies().get(0);

				joint = new WeldJoint(b, body, contactPoint);

				

				grippedCoin.setCrossedTeleportable(false);
				grippedCoin = coin;

				joints.add(joint);
				world.addJoint(joint);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeObject(World world) {
		super.initializeObject(world);
		this.world = world;

	}

	/**
	 * Drops the coin that the bascule is currently holding
	 */
	public void dropCoin() {

		world.removeJoint(joint);
		joints.remove(joint);
		if (grippedCoin != null) {
			// Sets the bottom has a sensor. It won't collide anymore.
			canCatch.set(false);
			

			grippedCoin.setCrossedTeleportable(true);
			grippedCoin = null;

			new Thread(() -> {
				// Wait for 1.5 seconds
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Remake it a collidable. It will be able to catch a coin
				// again.
				canCatch.set(true);

				// sets the current joint to null
				joint = null;

			}).start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAppliedJoint() {
		dropCoin();
	}
}
