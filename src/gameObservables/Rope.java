package gameObservables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import observables.AbstractComplexObservable;
import observables.ComplexObservable;
import observables.ScaleManager;

/**
 * A rope is a complex object composed of 2 bodies : The bottom and the top.
 * 
 * <p>
 * The top part is a fixed object in the space and should never move from its
 * position it the world.
 * <p>
 * The bottom part is an object attached to the top via a ropeJoint, and is
 * balancing along the distance given to the rope at construction.
 * 
 * <p>
 * This object initialize itself and its joints when the
 * {@link #initializeObject(World)} is called before adding it to the world.
 * 
 * <p>
 * A special consideration is made for the case in which the bottom part of the
 * rope would collide with a Coin. In the case that this event occurs, the Coin
 * would be attached the the bottom part via a WeldJoint. To remove this joint
 * from the attached objects, the method {@link #dropCoin()} must be called, and
 * will only have an effect when a Coin will be attached.
 * 
 * @author Sunny, Etienne, Mathieu
 *
 */
public class Rope extends ComplexObservable implements JointApplier {

	/**
	 * The dyn4j objects for collision purpose.
	 */
	private World world;
	/**
	 * The dyn4j objects for collision purpose. The joint that attach the coin
	 * and bottom body
	 */
	private WeldJoint stickJoint;
	/**
	 * The joint that keep together the top and bottom part of the rope. This
	 * joint also acts like an elastic.
	 */
	private RopeJoint joint;

	/**
	 * The rectangle of the top and bottom rope
	 */
	private Rectangle topRope, bottomRope;

	/**
	 * The bodies of the top and bottom rope.
	 */
	private Body topRopeBody, bottomRopeBody;

	/**
	 * The fixtures of the top and bottom rope.
	 */
	private BodyFixture topRopeFixture, bottomRopeFixture;

	/**
	 * The height of the rope in meters, defined at construction and can be
	 * modified during process via the {@link #setRopeHeight(double)} method
	 */
	private double height;

	/**
	 * Default width of the rope. This value is never changed
	 */
	private static final double WIDHT_ROPE = 0.3;

	/**
	 * a Boolean property telling either it is possible for the rope to catch a
	 * coin or not.
	 */
	private BooleanProperty canCatch;

	/**
	 * The coin that is attached to the rope. Only used to remove the joint
	 * between bodies when {@link #dropCoin()} is called.
	 */
	private Coin grippedCoin;

	/**
	 * The default height of the top part of the rope in meters.
	 */
	private static final double HEIGHT_TOP = 0.3;
	/**
	 * The default width of the top part of the rope in meters.
	 */
	private static final double WIDHT_TOP = 1;
	/**
	 * The default height of the bottom part of the rope in meters.
	 */
	private static final double HEIGHT_BOTTOM = 1.5;
	/**
	 * The default width of the bottom part of the rope in meters.
	 */
	private static final double WIDHT_BOTTOM = 1.7;

	/**
	 * All the properties for binding purpose. The value of those properties are
	 * represented in pixels, and use the ScaleManager class to handle the size
	 * of the objects.
	 */
	protected final DoubleProperty bottomTopLeftCornerX;
	protected final DoubleProperty bottomTopLeftCornerY;
	protected final DoubleProperty topTopLeftCornerX;
	protected final DoubleProperty topTopLeftCornerY;
	protected final DoubleProperty ropeHeight;
	protected final DoubleProperty ropeTopLeftCornerX;
	protected final DoubleProperty ropeTopLeftCornerY;
	protected final DoubleProperty bottomRotation;
	protected final DoubleProperty topRotation;
	protected final DoubleProperty ropeRotation;
	protected final DoubleProperty topHeight;
	protected final DoubleProperty topWidth;
	protected final DoubleProperty bottomHeight;
	protected final DoubleProperty bottomWidth;
	protected final DoubleProperty ropeWidth;
	protected final DoubleProperty angularVelocity;

	/**
	 * Default constructor of a rope. Constructs a rope with the rope's length
	 * specified in parameters. By default, a rope has the value of
	 * {@link #canCatch} to true;
	 * 
	 * @param pHeight
	 *            The length of the rope in meters.
	 */
	public Rope(double pHeight) {
		// Construct the rectangles
		this.topRope = new Rectangle(WIDHT_TOP, HEIGHT_TOP);
		this.bottomRope = new Rectangle(WIDHT_BOTTOM, HEIGHT_BOTTOM);

		// gives height parameter.
		this.height = pHeight;

		// Set the canCatch value to true
		this.canCatch = new SimpleBooleanProperty(true);

		// Creates the fixtures
		this.topRopeFixture = new BodyFixture(topRope);
		this.bottomRopeFixture = new BodyFixture(bottomRope);

		// Creates the bodies
		this.topRopeBody = new Body();
		topRopeBody.addFixture(topRopeFixture);
		this.bottomRopeBody = new Body();
		bottomRopeBody.addFixture(bottomRopeFixture);
		bottomRopeBody.rotate(Math.PI);

		// Sets bodies' mass
		topRopeBody.setMass(MassType.INFINITE);
		bottomRopeBody.setMass(new Mass(bottomRopeBody.getLocalCenter(), 0.0005, 50000));
		bottomRopeBody.translate(0, -1 * (pHeight + (HEIGHT_BOTTOM) / 2));

		// add them to the list
		bodies.add(topRopeBody);
		bodies.add(bottomRopeBody);

		// Initialize the properties
		bottomTopLeftCornerX = new SimpleDoubleProperty();
		bottomTopLeftCornerY = new SimpleDoubleProperty();
		topTopLeftCornerX = new SimpleDoubleProperty();
		topTopLeftCornerY = new SimpleDoubleProperty();
		ropeHeight = new SimpleDoubleProperty(ScaleManager.metersToPixels(height));
		ropeTopLeftCornerX = new SimpleDoubleProperty();
		ropeTopLeftCornerY = new SimpleDoubleProperty();
		bottomRotation = new SimpleDoubleProperty();
		topRotation = new SimpleDoubleProperty();
		ropeRotation = new SimpleDoubleProperty();
		topHeight = new SimpleDoubleProperty();
		topWidth = new SimpleDoubleProperty();
		bottomHeight = new SimpleDoubleProperty();
		bottomWidth = new SimpleDoubleProperty();
		ropeWidth = new SimpleDoubleProperty();
		angularVelocity = new SimpleDoubleProperty(0);

		this.setCrossedTeleportable(false);

		// Sets size property
		operateResize();
	}

	/**
	 * Called to update the properties of the rope. All the dimension properties
	 * are modified through this method, to ensure that the graphical object
	 * remains proportional to the dynamic object.
	 * 
	 * The angularFrequency of the rope is also computed.
	 */
	@Override
	public void update() {

		final Vector2 bottomCenter = bottomRopeBody.getTransform().getTranslation();
		final Vector2 topCenter = topRopeBody.getTransform().getTranslation();
		final Vector2 middlePoint = new Vector2((bottomCenter.x + topCenter.x) / 2, (bottomCenter.y + topCenter.y) / 2);

		// Difference between the 2 is the deltaX and deltaY used to compute the
		// rotation of the rope.
		double deltaX = bottomCenter.x - topCenter.x;
		double deltaY = bottomCenter.y - topCenter.y;

		// Pythagore distance
		final double distanceCenter = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		// We calculate the angle from a rectangular triangle and inverse it to
		// match the difference between position and calculated angle.
		double angleDegrees = -Math.toDegrees(Math.atan(deltaX / deltaY));

		topTopLeftCornerX.set(ScaleManager.metersToPixels(topCenter.x - (topRope.getWidth() / 2)));
		topTopLeftCornerY.set(ScaleManager.metersToPixels(topCenter.y - (topRope.getHeight() / 2)));
		topRotation.set(Math.toDegrees((topRopeBody.getTransform().getRotation())));

		ropeTopLeftCornerX.set(ScaleManager.metersToPixels(middlePoint.x - WIDHT_ROPE / 2));
		ropeTopLeftCornerY.set(ScaleManager.metersToPixels(middlePoint.y - distanceCenter / 2 + HEIGHT_TOP / 2));
		ropeRotation.set(angleDegrees);

		ropeHeight.set(ScaleManager.metersToPixels(distanceCenter - HEIGHT_TOP));

		bottomTopLeftCornerX.set(ScaleManager.metersToPixels(bottomCenter.x - (bottomRope.getWidth() / 2)));
		bottomTopLeftCornerY.set(ScaleManager.metersToPixels(bottomCenter.y - HEIGHT_BOTTOM / 2));
		bottomRotation.set(Math.toDegrees(bottomRopeBody.getTransform().getRotation()));

		if (world != null) {
			try {
				angularVelocity.set(Math.sqrt(-world.getGravity().y / height));
			} catch (Exception e) {
				angularVelocity.set(Math.sqrt(world.getGravity().y / height));
			}
		}

	}

	/**
	 * Initialize the rope in the world, including the joints that the rope
	 * requires to work properly.
	 */
	@Override
	public void initializeObject(World world) {
		super.initializeObject(world);
		this.world = world;
		if (world != null) {
			joint = new RopeJoint(topRopeBody, bottomRopeBody, topRopeBody.getWorldCenter(),
					bottomRopeBody.getWorldCenter());

			joint.setCollisionAllowed(true);
			joint.setLimits(0, height + HEIGHT_BOTTOM);

			world.addJoint(joint);
			joints.add(joint);
		}
	}

	/**
	 * 
	 * @param body
	 *            The body to verify
	 * @return True if the body sent in parameter is the magnet's body
	 */
	public boolean isMagnet(Body body) {
		return body == bottomRopeBody;

	}

	/**
	 * Handles the dynamic collide with an object. It is when this method is
	 * called that the rope creates the joint with the coin if the parameter
	 * object is from an instance of Coin, and if the rope doesn't already have
	 * a coin attaches to it.
	 */
	@Override
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {
		super.handleDynamicCollide(contactPoint, object, body, world);

		if (object instanceof Coin && body == bottomRopeBody) {
			createJoint((Coin) object);

		}
	}

	/**
	 * Rotates the rope from the angle sent in parameter, including the bottom
	 * and the top of the object. The object is rotate around the center.
	 */
	@Override
	public void rotate(double theta) {

		setBottomAngle(theta);

	}

	/**
	 * Translates the rope along the given vector. The rope is translate from
	 * the top body of the rope.
	 * 
	 * <p>
	 * Note that if the rope is attached to a coin, the translate won't be
	 * executed properly, but will remain normal after the {@link #dropCoin()}
	 * will be called.
	 */
	@Override
	public void translate(double x, double y) {

		final Vector2 actual = topRopeBody.getWorldCenter();

		topRopeBody.translate(-actual.x, -actual.y);
		bottomRopeBody.translate(-actual.x, -actual.y);

		topRopeBody.translate(x, y);
		bottomRopeBody.translate(x, y);

	}

	/**
	 * Gives to the bottom of the rope the given angle sent in parameter. The
	 * distance between the 2 will be equal to the length of the rope.
	 * 
	 * @param tetha
	 *            The angle given to the bottom block
	 */
	public void setBottomAngle(double tetha) {

		final double deltaX = ((height + WIDHT_BOTTOM / 2) * Math.cos(tetha));
		// Vertical drop is always negative
		final double deltaY = ((height + HEIGHT_BOTTOM / 2) * Math.sin(tetha));

		final Vector2 topPosition = topRopeBody.getWorldCenter();

		bottomRopeBody.getTransform().setTranslation(topPosition.x + deltaX, topPosition.y + deltaY);
	}

	/**
	 * Returns the angle formed by the rope between the bottom and top body
	 * parts.
	 * 
	 * @return The angle between the 2 parts in radians
	 */
	public double getBottomAngle() {
		double angle = 0;

		final Vector2 topPos = topRopeBody.getTransform().getTranslation();
		final Vector2 bottomPos = bottomRopeBody.getTransform().getTranslation();
		try {
			// atan (Delta Y / delta X)
			angle = Math.atan((bottomPos.y - topPos.y) / (bottomPos.x - topPos.x));

			// To recompute for the part atan doesn't reach
			if (bottomPos.x < topPos.x) {
				angle += Math.PI;
			}
		} catch (Exception e) {
			// occurs if the 2 bodies are horizontal. The angle depends on the
			// position of the bottom part
			if (bottomPos.y > topPos.y) {
				angle = Math.PI / 2;
			} else {
				angle = 3 * Math.PI / 2;
			}
		}
		while (angle < 0) {
			angle += 2 * Math.PI;
		}
		return angle;
	}

	/**
	 * Intern method that creates a joint between the Coin sent in parameter and
	 * the bottom of the rope. The coin is translate to the center of the bottom
	 * part, and is attached to it via {@link #stickJoint}.
	 * 
	 * @param pBody
	 *            The coin attached to the rope
	 */
	private void createJoint(Coin pBody) {

		// The coin must not have an infinite mass (means it would be stopped)
		if (stickJoint == null && canCatch.get() && pBody.getBodies().get(0).isDynamic()) {
			canCatch.set(false);
			Vector2 center = bottomRopeBody.getWorldCenter();
			pBody.translate(center.x, center.y - HEIGHT_BOTTOM / 2 - pBody.getRadius() / 2);
			this.grippedCoin = pBody;
			grippedCoin.setCrossedTeleportable(false);

			stickJoint = new WeldJoint(bottomRopeBody, pBody.getBodies().get(0), bottomRopeBody.getWorldCenter());
			stickJoint.setCollisionAllowed(false);

			joints.add(stickJoint);
			this.world.addJoint(stickJoint);
		}

	}

	/**
	 * Sets the rope height to the parameter's value. Note that the actual
	 * rotation of the bottom compared to the top part is conserved, and that
	 * the distance between the top and the bottom is instantly changed to the
	 * length of the rope.
	 * 
	 * @param height
	 *            The new height of the rope.
	 */
	public void setRopeHeight(double height) {

		final Vector2 bottomCenter = bottomRopeBody.getTransform().getTranslation();
		final Vector2 topCenter = topRopeBody.getTransform().getTranslation();

		// Difference between the 2 is the deltaX and deltaY used to compute the
		// rotation of the rope.
		double deltaX = bottomCenter.x - topCenter.x;
		double deltaY = bottomCenter.y - topCenter.y;

		// We calculate the angle from a rectangular triangle and inverse it to
		// match the difference between position and calculated angle.
		double angleRadians = (Math.atan(deltaY / deltaX));

		if (deltaX < 0) {
			angleRadians += Math.PI;
		}

		joint.setLimits(0, height + HEIGHT_BOTTOM);
		this.height = height;

		setBottomAngle(angleRadians);

	}

	/**
	 * Drop the coin attached to bottom. After the coin is dropped, a 1.5second
	 * thread in launched, and the value of {@link #canCatch} is set to false
	 * during that process, in order that the coin do not stick again with the
	 * rope immediately.
	 * 
	 * <p>
	 * When the thread is over, the value of {@link #canCatch} is set back to
	 * true, and another coin can therefore collide with the rope.
	 */
	public void dropCoin() {
		if (grippedCoin != null) {
			
			grippedCoin.setCrossedTeleportable(true);
			world.removeJoint(stickJoint);
			joints.remove(stickJoint);
			
			// Sets the bottom has a sensor. It won't collide anymore.
			canCatch.set(false);
			grippedCoin = null;

			new Thread(() -> {
				// Wait for 1.5 seconds
				try {
					Thread.sleep(1500);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Remake it a collidable. It will be able to catch a coin
				// again.
				canCatch.set(true);

				// sets the current joint to null
				stickJoint = null;

			}).start();
		}
	}

	/**
	 * 
	 * @return A property representing the value of the capacity of the rope to
	 *         catch a Coin. A false value means the coin can't be catch, and a
	 *         true value means the rope can catch a Coin.
	 */
	public ReadOnlyBooleanProperty canCatchProperty() {
		return canCatch;
	}

	/**
	 * 
	 * @return A boolean representing the value of the capacity of the rope to
	 *         catch a Coin. A false value means the coin can't be catch, and a
	 *         true value means the rope can catch a Coin.
	 */
	public boolean canCatch() {
		return canCatch.get();
	}

	/**
	 * Returns the actual coin gripped by the rope. This value is always null
	 * unless the
	 * {@link #handleDynamicCollide(AbstractComplexObservable, Body, World)} is
	 * called with the AbstractComplexObservable parameter from the instance of
	 * Coin is called.
	 * 
	 * @return The actual gripped Coin, or null if no Coin is gripped.
	 */
	public Coin getGrippedCoin() {
		return grippedCoin;
	}

	/**
	 * Returns the rotation of the rope in radians.
	 */
	@Override
	public double getRotate() {
		// TODO Auto-generated method stub
		return getBottomAngle();
	}

	/**
	 * Returns the translation of the rope in meters in a vector.
	 */
	@Override
	public Vector2 getTranslate() {
		// TODO Auto-generated method stub
		return topRopeBody.getTransform().getTranslation();
	}

	/**
	 * Called when a resize is required, and is called via the ScaleManager
	 * class.
	 */
	@Override
	public void operateResize() {
		topHeight.set(ScaleManager.metersToPixels(HEIGHT_TOP));
		topWidth.set(ScaleManager.metersToPixels(WIDHT_TOP));
		bottomHeight.set(ScaleManager.metersToPixels(HEIGHT_BOTTOM));
		bottomWidth.set(ScaleManager.metersToPixels(WIDHT_BOTTOM));
		ropeWidth.set(ScaleManager.metersToPixels(WIDHT_ROPE));
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The top left position x of the bottom body
	 */
	public final ReadOnlyDoubleProperty bottomTopLeftCornerXProperty() {
		return this.bottomTopLeftCornerX;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The top left position y of the bottom body
	 */
	public final ReadOnlyDoubleProperty bottomTopLeftCornerYProperty() {
		return this.bottomTopLeftCornerY;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The top left position x of the top body
	 */
	public final ReadOnlyDoubleProperty topTopLeftCornerXProperty() {
		return this.topTopLeftCornerX;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The top left position y of the top body
	 */
	public final ReadOnlyDoubleProperty topTopLeftCornerYProperty() {
		return this.topTopLeftCornerY;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The height of the rope
	 */
	public final ReadOnlyDoubleProperty ropeHeightProperty() {
		return this.ropeHeight;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The top left position x of the rope
	 */
	public final ReadOnlyDoubleProperty ropeTopLeftCornerXProperty() {
		return this.ropeTopLeftCornerX;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The top left position y of the rope
	 */
	public final ReadOnlyDoubleProperty ropeTopLeftCornerYProperty() {
		return this.ropeTopLeftCornerY;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The rotation of the bottom body
	 */
	public final ReadOnlyDoubleProperty bottomRotationProperty() {
		return this.bottomRotation;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The rotation of the top body
	 */
	public final ReadOnlyDoubleProperty topRotationProperty() {
		return this.topRotation;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The rotation of the rope
	 */
	public final ReadOnlyDoubleProperty ropeRotationProperty() {
		return this.ropeRotation;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The height of the top
	 */
	public final ReadOnlyDoubleProperty topHeightProperty() {
		return this.topHeight;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The width of the top
	 */
	public final ReadOnlyDoubleProperty topWidthProperty() {
		return this.topWidth;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The height of the bottom
	 */
	public final ReadOnlyDoubleProperty bottomHeightProperty() {
		return this.bottomHeight;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The width of the bottom
	 */
	public final ReadOnlyDoubleProperty bottomWidthProperty() {
		return this.bottomWidth;
	}

	/**
	 * Javafx binding property in pixels. Updated automatically via the
	 * {@link #update()} method.
	 * 
	 * @return The width of the rope
	 */
	public final ReadOnlyDoubleProperty ropeWidthProperty() {
		return this.ropeWidth;
	}

	/**
	 * Returns the angular velocity of the rope. This velocity is calculated
	 * using sqrt({@link #height}/world.gravity.y)
	 * 
	 * @return The angular velocity as a property
	 */
	public final ReadOnlyDoubleProperty angularVelocityProperty() {
		return this.angularVelocity;
	}

	/**
	 * Method for test purpose. Should never be called outside tests
	 * 
	 * @deprecated
	 * @return
	 */
	public Body bottomRopeBody() {
		// TODO Auto-generated method stub
		return bottomRopeBody;
	}

	/**
	 * Method for test purpose. Should never be called outside tests
	 * 
	 * @deprecated
	 * @return
	 */
	public Rectangle getBottomRope() {
		// TODO Auto-generated method stub
		return bottomRope;
	}

	/**
	 * Method for test purpose. Should never be called outside tests
	 * 
	 * @deprecated
	 * @return
	 */
	public Body getBottomRopeBody() {
		// TODO Auto-generated method stub
		return bottomRopeBody;
	}

	/**
	 * Method for test purpose. Should never be called outside tests
	 * 
	 * @deprecated
	 * @return
	 */
	public Body getTopRopeBody() {
		// TODO Auto-generated method stub
		return topRopeBody;
	}

	/**
	 * Method for test purpose. Should never be called outside tests
	 * 
	 * @deprecated
	 * @return
	 */
	public Rectangle getTopRope() {
		// TODO Auto-generated method stub
		return topRope;
	}

	public double getBottomRotation() {
		// TODO Auto-generated method stub
		return bottomRopeBody.getTransform().getRotation();
	}

	public double getTopRotation() {
		// TODO Auto-generated method stub
		return topRopeBody.getTransform().getRotation();
	}

	/**
	 * @return The height of the rope
	 */
	public double getRopeHeight() {
		return height;
	}

	/**
	 * Removes the joints that the rope might possibly have put on a Coin.
	 */
	@Override
	public void removeAppliedJoint() {
		dropCoin();
	}

}
