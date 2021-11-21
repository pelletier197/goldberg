package gameObservables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;

import observables.Surface;

/**
 * A Bascule is basically a Surface that is rotating around a fixed point
 * situated in its center.
 * 
 * @author Sunny, Etienne, Mathieu
 *
 */
public class Bascule extends Surface {
	/**
	 * Dyn4j physicObject for the fixation point positioned in the center of the
	 * object.
	 */
	protected Circle fix;
	/**
	 * Dyn4j physicObject for the fixation point positioned in the center of the
	 * object.
	 */
	protected BodyFixture fixture;
	/**
	 * Dyn4j physicObject for the fixation point positioned in the center of the
	 * object.
	 */
	protected Body fixBascule;

	/**
	 * Minimal constructor for a Bascule.
	 * 
	 * @param width
	 *            The width of the Bascule
	 * @param height
	 *            The height of the Bascule
	 */
	public Bascule(double width, double height) {
		this(width, height, 0.00001);
	}

	/**
	 * Minimal constructor for a Bascule.
	 * 
	 * @param width
	 *            The width of the Bascule
	 * @param height
	 *            The height of the Bascule
	 */
	public Bascule(double width, double height, double mass) {
		super(width, height);
		// Allow movements for Bascule. The mass is extremely low to allow
		// movement
		super.body.setMass(new Mass(super.body.getLocalCenter(), 1000, 0.001));
		super.body.setBullet(false);

		// creates the properties of the fix joint in the center
		this.fix = new Circle(0.000001);
		this.fixture = new BodyFixture(fix);

		this.fixBascule = new Body();
		this.fixBascule.setBullet(true);

		this.fixBascule.translate(super.body.getWorldCenter());
		this.fixBascule.addFixture(fixture);
		this.fixBascule.setMass(MassType.INFINITE);

		bodies.add(fixBascule);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void translate(double x, double y) {
		this.fixBascule.getTransform().setTranslation(x, y);
		super.translate(x, y);
	}

	/**
	 * Initialize the Bascule in the world. Simply add the revolute joint to the
	 * world.
	 */
	@Override
	public void initializeObject(World world) {
		super.initializeObject(world);

		RevoluteJoint joint = new RevoluteJoint(this.fixBascule, super.body, fixBascule.getWorldCenter());
		joint.setCollisionAllowed(false);
		world.addJoint(joint);

		joints.add(joint);
	}

	/**
	 * 
	 * @return The inertia of the Bascule, considered as mass, as it is what
	 *         makes it rotate around the point. Mass is useless paramter.
	 */
	public double getMass() {
		return super.body.getMass().getInertia();
	}

	/**
	 * Sets the bascule inertia considered as mass.
	 * 
	 * @param mass
	 *            The new mass value.
	 */
	public void setMass(double mass) {
		Mass actualMass = super.body.getMass();
		super.body.setMass(new Mass(actualMass.getCenter(), mass, mass));
	}

}
