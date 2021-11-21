package gameObservables;

import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import observables.AbstractComplexObservable;
import observables.ComplexObservable;
import observables.ScaleManager;

/**
 * A spring is a dyn4j object that is made of to Rectangle, joined together by a
 * DistanceJoint and a PrismaticJoint.
 * 
 * The Spring class is designed to run with javaFx graphics. Therefore, the
 * properties height and width properties are made in order to ensure that the
 * javaFx object stays proportional to the spring dimensions.
 * 
 * The dimensions are computed every time the method {@link #update()} is
 * called, normally over a background thread.
 * 
 * On the other hand, the spring is completely customizable : properties are
 * available for the springConstant, the mass of the mobile plate. The spring
 * can also be moved via the {@link #translate(double, double)} method, or
 * rotated via the {@link #rotate(double)} method.
 * 
 * Important : It is imperative that the method {@link #initializeObject(World)}
 * get called, or the object will never be initialized properly in the world.
 * 
 * @author Sunny Pelletier, Etienne Matteau
 *
 */
public class Spring extends ComplexObservable {

	/**
	 * {@link #HEIGHT_CONSERVATION_RATIO} is the conservation ratio of the
	 * height that is applied to the base of the spring. The base height will
	 * then be equal to weightConservationRatio*heightOfPlate
	 */
	private static final double HEIGHT_CONSERVATION_RATIO = 0.60;
	private static final double SPRING_REST_LENGHT_RATIO = 0.60;
	private static final double INITIAL_PLATE_MASS = 0.01;

	/**
	 * Dyn4j dynamic objects
	 */
	private DistanceJoint springEffect;
	private PrismaticJoint linearJoint;
	private Gjk distance;
	private Rectangle plate, base;
	private BodyFixture plateFixture, baseFixture;
	private Body plateBody, baseBody;

	/**
	 * All the properties for graphic purpose. The position representation are
	 * in pixel format. The rotation representation are in degrees.
	 */
	protected final DoubleProperty baseTopLeftCornerX;
	protected final DoubleProperty baseTopLeftCornerY;
	protected final DoubleProperty plateTopLeftCornerX;
	protected final DoubleProperty plateTopLeftCornerY;
	protected final DoubleProperty rotation;
	protected final DoubleProperty springLength;
	protected final DoubleProperty springHeight;
	protected final DoubleProperty springTopLeftCornerX;
	protected final DoubleProperty springTopLeftCornerY;
	protected final DoubleProperty baseRotation;
	protected final DoubleProperty plateRotation;
	protected final DoubleProperty baseWidth;
	protected final DoubleProperty baseHeight;
	protected final DoubleProperty plateWidthP;
	protected final DoubleProperty plateHeightP;

	/**
	 * Private spring measures of dimensions. Those variable are in meters
	 */
	private double height, width, springHeightMeters, springLengthMeters, plateHeight, widthDifference, plateWidth;
	/**
	 * Properties for springConstant, plateMass and force
	 */
	private DoubleProperty springConstant, plateMass, force;

	/**
	 * The boolean property telling either the spring is a detonator or not. A
	 * spring is considered a detonator, when a dynamic collide with an object
	 * will make it unlock automatically.
	 */
	private final BooleanProperty detonator;

	/**
	 * The locked value. True by default at construction
	 */
	private boolean locked;

	/**
	 * The constructor of a spring. A spring is a dyn4j object that is made of
	 * to Rectangle, joined together by a DistanceJoint and a PrismaticJoint.
	 * 
	 * By default, the translate is set to (0,0), and the spring is on the
	 * locked mode.
	 * 
	 * @param springConstant
	 *            The spring constant of the spring
	 * @param height
	 *            The height of the spring. Not all the part of the spring are
	 *            equal to this size
	 * @param width
	 *            The width of the spring. The overall width of the spring.
	 */
	public Spring(double springConstant, double height, double width) {
		super();
		/*
		 * Assigns the constants
		 */
		this.height = height;
		this.width = width;
		plateHeight = height;
		widthDifference = 1 - HEIGHT_CONSERVATION_RATIO;
		springLengthMeters = width * SPRING_REST_LENGHT_RATIO;
		plateWidth = (width - springLengthMeters) / 2;
		springHeightMeters = plateHeight / 4;

		// The spring is locked by default
		locked = true;

		this.setCrossedTeleportable(false);

		// Create the dyn4j shapes
		this.plate = new Rectangle(plateWidth, plateHeight);
		this.base = new Rectangle(plateWidth, HEIGHT_CONSERVATION_RATIO * height);

		// Gives them modifiable fixtures
		this.plateFixture = new BodyFixture(plate);
		this.baseFixture = new BodyFixture(base);
		baseFixture.setRestitution(0);
		baseFixture.setFriction(1111111);

		// Give the fixtures to the bodies
		this.baseBody = new Body();
		baseBody.addFixture(baseFixture);
		this.plateBody = new Body();
		plateBody.addFixture(plateFixture);

		// we set the plate as a bullet, can be extremely fast
		plateBody.setBullet(true);

		// Give translate to the plate
		// This translate is equal to the width of the base + with of spring +
		// width of plate /2
		plateBody.translate(new Vector2(width - plateWidth, widthDifference * height / 2));

		baseBody.translate(new Vector2(0, widthDifference * height / 2));

		// Gives them respective masses
		baseBody.setMass(MassType.INFINITE);
		plateBody.setMass(new Mass(plate.getCenter(), INITIAL_PLATE_MASS, 5));

		plateBody.getGravityScale();

		// add them to the list
		bodies.add(baseBody);
		bodies.add(plateBody);

		this.distance = new Gjk();

		/*
		 * Instantiate the properties and their listeners.
		 */
		baseTopLeftCornerX = new SimpleDoubleProperty();
		baseTopLeftCornerY = new SimpleDoubleProperty();
		plateTopLeftCornerX = new SimpleDoubleProperty();
		plateTopLeftCornerY = new SimpleDoubleProperty();
		rotation = new SimpleDoubleProperty();
		springLength = new SimpleDoubleProperty();
		springHeight = new SimpleDoubleProperty(ScaleManager.metersToPixels(springHeightMeters));
		springTopLeftCornerX = new SimpleDoubleProperty();
		springTopLeftCornerY = new SimpleDoubleProperty();
		baseRotation = new SimpleDoubleProperty();
		plateRotation = new SimpleDoubleProperty(0);
		plateHeightP = new SimpleDoubleProperty();
		plateWidthP = new SimpleDoubleProperty();
		baseHeight = new SimpleDoubleProperty();
		baseWidth = new SimpleDoubleProperty();
		detonator = new SimpleBooleanProperty(false);

		this.springConstant = new SimpleDoubleProperty(springConstant);
		this.springConstant.addListener((value, old, newv) -> setJointProperties());

		this.plateMass = new SimpleDoubleProperty(INITIAL_PLATE_MASS);
		this.plateMass.addListener((value, old, newv) -> setPlateMass(newv.doubleValue()));

		this.rotation.addListener((value, old, newv) -> rotate(Math.toDegrees(newv.doubleValue())));

		this.force = new SimpleDoubleProperty(0);

		// resize the size properties
		operateResize();

	}

	/**
	 * Called to update the properties of the spring. All the dimension
	 * properties are modified through this method, to ensure that the graphical
	 * object remains proportional to the dynamic object.
	 * 
	 * The force of the spring is also computed.
	 */
	@Override
	public void update() {

		baseTopLeftCornerX.set(ScaleManager.metersToPixels(baseBody.getWorldCenter().x - plateWidth / 2));
		baseTopLeftCornerY
				.set(ScaleManager.metersToPixels(baseBody.getTransform().getTranslationY() - base.getHeight() / 2));
		baseRotation.set(Math.toDegrees(baseBody.getTransform().getRotation()));

		plateTopLeftCornerX.set(ScaleManager.metersToPixels(plateBody.getWorldCenter().x - plateWidth / 2));
		plateTopLeftCornerY
				.set(ScaleManager.metersToPixels(plateBody.getTransform().getTranslationY() - plate.getHeight() / 2));
		plateRotation.set(Math.toDegrees(plateBody.getTransform().getRotation()) + 180);

		// Updates the spring width depending on the distance between plates
		final Separation distanceCalculator = new Separation();
		distance.distance(base, baseBody.getInitialTransform(), plate, plateBody.getInitialTransform(),
				distanceCalculator);

		Vector2 middlePoint = new Vector2((baseBody.getWorldCenter().x + plateBody.getWorldCenter().x) / 2,
				(baseBody.getWorldCenter().y + plateBody.getWorldCenter().y) / 2);

		springLength.set(ScaleManager.metersToPixels(distanceCalculator.getDistance()));
		// translation of spring in x
		springTopLeftCornerX.set(ScaleManager.metersToPixels(middlePoint.x - distanceCalculator.getDistance() / 2));

		// translation of spring in y
		springTopLeftCornerY.set(ScaleManager.metersToPixels(middlePoint.y - springHeightMeters / 2));

		// The force of the spring is equal to F = -kx
		force.set(-(springLengthMeters - distanceCalculator.getDistance()) * springConstant.get());

	}

	/**
	 * Handles the dynamic collide of the spring. If the spring is set as a
	 * detonator, it is going to unlock at this moment.
	 */
	@Override
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {
		super.handleDynamicCollide(contactPoint, object, body, world);
		if (isDetonator() && isLocked() && body == plateBody && this != object) {
			unlock();
		}
	}

	/**
	 * Sets the value of {@link #detonator}
	 * 
	 * @param detonator
	 *            The detonator value
	 */
	public void setDetonator(boolean detonator) {
		this.detonator.set(detonator);
	}

	/**
	 * See {@link #detonator} for more details about detonator
	 * 
	 * @return The boolean property holding the detonator value.
	 */
	public BooleanProperty detonatorProperty() {
		isDetonator();
		return this.detonator;
	}

	/**
	 * See {@link #detonator} for more details about detonator
	 * 
	 * @return True if the spring is detonator, false otherwise
	 */
	public boolean isDetonator() {
		// TODO Auto-generated method stub
		return detonator.get();
	}

	/**
	 * Called by the world. This method must always be called before using the
	 * object. It will allow the spring to instantiate it's joints in the world.
	 * 
	 * If this method is not called, the object will not exist in the world.
	 * This method should normally be called once during the process, but can be
	 * called more than once without any problem.
	 */
	@Override
	public void initializeObject(World world) {
		super.initializeObject(world);

		if (world != null) {

			// remove old joints from list and world.
			world.removeJoint(linearJoint);
			world.removeJoint(springEffect);
			joints.remove(linearJoint);
			joints.remove(springEffect);

			// sets the translate along the vector that is perpendicular to the
			// plate and the base.

			linearJoint = new PrismaticJoint(plateBody, baseBody, plateBody.getWorldCenter(),
					new Vector2(baseBody.getWorldCenter().x - plateBody.getWorldCenter().x,
							baseBody.getWorldCenter().y - plateBody.getWorldCenter().y));

			// Give joint properties
			linearJoint.setCollisionAllowed(true);
			linearJoint.setLimits(0, springLengthMeters / 1.5);
			linearJoint.setReferenceAngle(0);

			// Creates the spring effect depending on the spring constant
			springEffect = new DistanceJoint(plateBody, baseBody, plateBody.getWorldCenter(),
					baseBody.getWorldCenter());
			springEffect.setCollisionAllowed(false);
			setJointProperties();

			// add the joints to the world and list
			world.addJoint(springEffect);
			world.addJoint(linearJoint);
			joints.add(springEffect);
			joints.add(linearJoint);
		}
	}

	/**
	 * Rotate the spring from the center of the spring. The center is defined as
	 * the average point between {@link #plateBody} and {@link #baseBody}. Both
	 * bodies are rotated around this point.
	 * 
	 * Note that the theta parameter is not simply added to the actual rotation,
	 * but is defined as the new rotation angle.
	 * 
	 * @param theta
	 *            The angle in radians.
	 */
	@Override
	public void rotate(double theta) {

		// Make the 2 shapes rotate around that point
		baseBody.getTransform().setRotation(theta);
		plateBody.getTransform().setRotation(theta);

	}

	/**
	 * Translate the Spring along the position. The spring rotation and locked
	 * state is conserved.
	 * 
	 * Note that the translation is not simple added to the actual translation,
	 * but is defined as the new translation.
	 * 
	 * @param x
	 *            The x position in meters
	 * @param y
	 *            The y position in meters
	 */
	@Override
	public void translate(double x, double y) {

		double distanceX = plateBody.getTransform().getTranslationX() - baseBody.getTransform().getTranslationX();
		double distanceY = plateBody.getTransform().getTranslationY() - baseBody.getTransform().getTranslationY();

		// Translate to the origin, depending on the actual translation
		baseBody.getTransform().setTranslation(x, y);
		plateBody.getTransform().setTranslation(x + distanceX, y + distanceY);

	}

	/**
	 * Sets the spring constant of this spring. The modification of this value
	 * forces the joint's properties to be modified.
	 * 
	 * @param k
	 *            The new spring constant in N/m²
	 */
	public void setSpringConstant(double k) {
		this.springConstant.set(k);
	}

	/**
	 * The spring force property. The force is equal to -kx, where x is the
	 * difference between expended size of the spring and the distance between
	 * the 2 plates.
	 * 
	 * @return A readOnlyProperty of the spring force.
	 */
	public ReadOnlyDoubleProperty forceProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(force);
	}

	/**
	 * The plateMass property returned as modifiable property.
	 */
	public DoubleProperty plateMassProperty() {
		return plateMass;
	}

	/**
	 * 
	 * 
	 * @return The spring constant of this spring as a property
	 */
	public DoubleProperty springConstantProperty() {
		return springConstant;
	}

	/**
	 * Method that modify the joints' properties every time the spring constant
	 * is changed. Note that this method will have no effect if the joints are
	 * null.
	 * 
	 * The dampingEffect and frequency from {@link #springEffect} are computed
	 * in order to give a realistic effect to the spring.
	 */
	private void setJointProperties() {
		if (springEffect != null) {

			if (springConstant.get() <= 0) {
				// The spring contract under everything
				springEffect.setDampingRatio(0.0000000001);
				springEffect.setFrequency(0.000000001);

			} else {
				try {
					springEffect.setDampingRatio(springConstant.get() / 100);
				} catch (Exception e) {
					springEffect.setDampingRatio(1);
				}
				springEffect.setFrequency(springConstant.get() / 4);

			}
			if (locked) {
				// Sets the dampling and frequency depending on the spring
				// constant
				springEffect.setDistance(plateWidth + 0.05);
			} else {
				springEffect.setDistance(springLengthMeters + plateWidth);
			}
		}
	}

	/**
	 * Sets the mass of the plate that moves and collide.
	 * 
	 * @param mass
	 *            The new mass
	 */
	public void setPlateMass(double mass) {
		plateBody.setMass(new Mass(plate.getCenter(), mass, 5));
		plateMass.set(mass);
	}

	/**
	 * Returns the value of {@link #locked}. A locked spring will be completely
	 * closed and won't collide anymore with a spring effect.
	 * 
	 * @return True if the spring is locked, false otherwise
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Locks the spring. The spring won't have a spring effect anymore.
	 */
	public void lock() {
		locked = true;
		setJointProperties();

	}

	/**
	 * Unlock the spring. If it was previously locked, the spring will get high
	 * acceleration and will produce an enormous force depending of the mass of
	 * the plate.
	 */
	public void unlock() {
		locked = false;
		setJointProperties();
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return base x position
	 */
	public final ReadOnlyDoubleProperty baseTopLeftCornerXProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.baseTopLeftCornerX);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return base y position
	 */
	public final ReadOnlyDoubleProperty baseTopLeftCornerYProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.baseTopLeftCornerY);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return plate x position
	 */
	public final ReadOnlyDoubleProperty plateTopLeftCornerXProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.plateTopLeftCornerX);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return plate y position
	 */
	public final ReadOnlyDoubleProperty plateTopLeftCornerYProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.plateTopLeftCornerY);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return the spring's rotation in degrees
	 */
	public final DoubleProperty rotationProperty() {
		return this.rotation;
	}

	/**
	 * 
	 * @return The spring's rotation in degrees
	 */
	public final double getRotation() {
		return this.rotationProperty().get();
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The length (or width) of the spring
	 */
	public final ReadOnlyDoubleProperty springLengthProperty() {
		return this.springLength;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The height of the spring
	 */
	public final ReadOnlyDoubleProperty springHeightProperty() {
		return this.springHeight;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The x position of the spring's view
	 */
	public final ReadOnlyDoubleProperty springTopLeftCornerXProperty() {
		return this.springTopLeftCornerX;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The x position of the spring's view
	 */
	public final ReadOnlyDoubleProperty plateWidthProperty() {
		return this.plateWidthP;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The x position of the spring's view
	 */
	public final ReadOnlyDoubleProperty plateHeightProperty() {
		return this.plateHeightP;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The x position of the spring's view
	 */
	public final ReadOnlyDoubleProperty baseWidthProperty() {
		return this.baseWidth;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The x position of the spring's view
	 */
	public final ReadOnlyDoubleProperty baseHeightProerty() {
		return this.baseHeight;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The y position of the spring's view
	 */
	public final ReadOnlyDoubleProperty springTopLeftCornerYProperty() {
		return this.springTopLeftCornerY;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The rotation of the base
	 */

	public final ReadOnlyDoubleProperty baseRotationProperty() {
		return this.baseRotation;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The rotation of the plate
	 */
	public final ReadOnlyDoubleProperty plateRotationProperty() {
		return this.plateRotation;
	}

	@Override
	public void setMobile(boolean mobile) {
		if (mobile) {
			for (Body b : bodies) {
				b.setActive(true);
				b.setAsleep(false);
			}
		}
	}

	@Override
	public double getRotate() {
		// TODO Auto-generated method stub
		return plateBody.getTransform().getRotation();
	}

	@Override
	public Vector2 getTranslate() {
		// TODO Auto-generated method stub
		return baseBody.getTransform().getTranslation();
	}

	/**
	 * methods for tests
	 */
	public DistanceJoint getSpringEffect() {
		return springEffect;
	}

	/**
	 * Dyn4j joint for a linear movement
	 * 
	 * @return the linear joint
	 */
	public PrismaticJoint getLinearJoint() {
		return linearJoint;
	}

	/**
	 * 
	 * @return a property of the spring constant.
	 */
	public DoubleProperty getSpringConstant() {
		return springConstant;
	}

	public Body getPlateBody() {
		return plateBody;
	}

	public Body getBaseBody() {
		return baseBody;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The top left positionX of the base
	 */
	public DoubleProperty getBaseTopLeftCornerX() {
		return baseTopLeftCornerX;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The top left positionY of the base
	 */
	public DoubleProperty getBaseTopLeftCornerY() {
		return baseTopLeftCornerY;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The top left positionX of the plate
	 */
	public DoubleProperty getPlateTopLeftCornerX() {
		return plateTopLeftCornerX;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The top left positionY of the plate
	 */
	public DoubleProperty getPlateTopLeftCornerY() {
		return plateTopLeftCornerY;
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The base rotation in degrees as a property
	 */
	public DoubleProperty getBaseRotation() {
		return baseRotation;
	}

	public DoubleProperty getPlateRotation() {
		return plateRotation;
	}

	public double getPlateWidth() {
		return plateWidth;
	}

	public Rectangle getBase() {
		return base;
	}

	public Rectangle getPlate() {
		return plate;
	}

	public DoubleProperty getForce() {
		return force;
	}

	@Override
	public void operateResize() {

		// Updates the size of the spring depending on the spring's size
		plateWidthP.set(ScaleManager.metersToPixels(plateWidth));
		plateHeightP.set(ScaleManager.metersToPixels(this.plateHeight));
		baseWidth.set(ScaleManager.metersToPixels(plateWidth));
		baseHeight.set(ScaleManager.metersToPixels(base.getHeight()));
		springHeight.set(ScaleManager.metersToPixels(springHeightMeters));

	}

	/**
	 * 
	 * @return the height of the spring in meter value.
	 */
	public double getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	/**
	 * 
	 * @return the width of the spring in meter value.
	 */
	public double getWidth() {
		return width;
	}
}
