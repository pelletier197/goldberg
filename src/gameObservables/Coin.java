package gameObservables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import observables.ComplexObservable;
import observables.ScaleManager;

/**
 * A Coin is a Circle that collides in the world. It contains properties that
 * might be binded to a javaFx view. Those properties are set as readOnly, to
 * ensure that the engine is the only object that modify those properties.
 * 
 * @author sunny
 *
 */
public class Coin extends ComplexObservable {

	/**
	 * Dyn4j dynamic objects
	 */
	private Circle object;
	private Body body;
	private BodyFixture fixture;

	/**
	 * Optimized physic parameters for good looking effect
	 */
	public static final double RESTITUTION_COEF = 0.4;
	public static final double MASS = 0.0005;
	public static final double DENSITY = 0.005;
	public static final double FRICTION = 0.3;
	public static final double INERTIA = 0.003;

	/**
	 * The mass of the coin in Kg
	 */
	private DoubleProperty mass;

	public static final Mass COIN_MASS = new Mass(new Vector2(0, 0), MASS, INERTIA);

	/**
	 * Position x,y and rotation in pixels and degrees for javaFx purpose
	 */
	private DoubleProperty topLeftCornerX, topLeftCornerY, rotation, radius;

	/**
	 * Minimal constructor of a Coin
	 * 
	 * @param radius
	 *            The radius of the Coin in meters
	 */
	public Coin(double radius) {
		this(radius, 0, 0);

	}

	/**
	 * Construct a coin, with the parameters sent.
	 * 
	 * @param radius
	 *            The radius of the Coin in meters
	 * @param positionX
	 *            The x position in meters
	 * @param positionY
	 *            The y position in meters
	 */
	public Coin(double radius, double positionX, double positionY) {
		this(radius, positionX, positionY, 0);
	}

	/**
	 * 
	 * Construct a coin, with the parameters sent.
	 * 
	 * @param radius
	 *            The radius of the Coin in meters
	 * @param positionX
	 *            The x position in meters
	 * @param positionY
	 *            The y position in meters
	 * @param rotation
	 *            The initial rotation of the Coin
	 */
	public Coin(double radius, double positionX, double positionY, double rotation) {
		super();

		object = new Circle(radius);

		// Creates the bodyficture
		fixture = new BodyFixture(object);
		fixture.setRestitution(RESTITUTION_COEF);
		fixture.setFriction(FRICTION);
		fixture.setDensity(DENSITY);

		// Creates the body
		body = new Body();
		body.setMass(COIN_MASS);
		body.setAngularDamping(0.5);

		// add it to the body
		body.addFixture(fixture);

		// Body added to the list
		bodies.add(body);

		// The mass listener
		mass = new SimpleDoubleProperty(MASS);
		mass.addListener((value, old, newv) -> body.setMass(new Mass(object.getCenter(), newv.doubleValue(), INERTIA)));

		topLeftCornerX = new SimpleDoubleProperty(0);
		topLeftCornerY = new SimpleDoubleProperty(0);
		this.rotation = new SimpleDoubleProperty(0);
		this.radius = new SimpleDoubleProperty(ScaleManager.metersToPixels(radius));

		// Translate and rotate to the given position
		translate(positionX, positionY);
		rotate(rotation);
	}

	/**
	 * Updates the Coin's properties in order to keep the view proportional to
	 * the coin's dimensions.
	 */
	@Override
	public void update() {

		topLeftCornerX.set(ScaleManager.metersToPixels((body.getTransform().getTranslationX() - object.getRadius())));
		topLeftCornerY.set(ScaleManager.metersToPixels((body.getTransform().getTranslationY() - object.getRadius())));
		rotation.set(Math.toDegrees(body.getTransform().getRotation()) + 180);

	}

	/**
	 * Sets the mass of the coin
	 * 
	 * @param mass
	 */
	public void setMass(double mass) {
		this.mass.set(mass);
	}

	/**
	 * Stops the coin movement in the world
	 */
	public void stopMovement() {
		body.setMass(MassType.INFINITE);
		body.setLinearVelocity(new Vector2(0, 0));
		body.setAngularVelocity(0);
		mass.set(Double.POSITIVE_INFINITY);
	}

	/**
	 * Makes the coin rotate to the theta angle
	 * 
	 * @param theta
	 *            The rotation angle in radians
	 */
	@Override
	public void rotate(double theta) {
		// Rotate to original rotation
		body.getTransform().setRotation(theta);

	}

	/**
	 * Makes the coin translate to the specified position
	 * 
	 * @param x
	 *            The new x translation
	 * @param y
	 *            The new y translation
	 */
	@Override
	public void translate(double x, double y) {

		body.getTransform().setTranslation(x, y);

	}

	/**
	 * The mass property of the Coin
	 * 
	 * @return The {@link #mass} field in Kg.
	 */
	public final DoubleProperty massProperty() {
		return this.mass;
	}

	/**
	 * 
	 * @return the {@link #mass} value is Kg
	 */
	public final double getMass() {
		return this.massProperty().get();
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The position x of the Coin
	 */
	public final ReadOnlyDoubleProperty topLeftCornerXProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.topLeftCornerX);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The position y of the Coin
	 */
	public final ReadOnlyDoubleProperty topLeftCornerYProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.topLeftCornerY);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The position y of the Coin
	 */
	public final ReadOnlyDoubleProperty radiusProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.radius);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The rotation of the Coin
	 */
	public final ReadOnlyDoubleProperty rotationProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.rotation);
	}

	@Override
	public double getRotate() {

		return body.getTransform().getRotation();
	}

	@Override
	public Vector2 getTranslate() {

		return body.getWorldCenter();
	}

	@Override
	public void operateResize() {
		radius.set(ScaleManager.metersToPixels(object.getRadius()));

	}

	public double getRadius() {
		return object.getRadius();
	}

}
