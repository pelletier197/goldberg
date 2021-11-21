package observables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A Surface is a Rectangle that collides in the world. It contains properties
 * that might be binded to a javaFx view. Those properties are set as readOnly,
 * to ensure that the engine is the only object that modify those properties.
 *
 * 
 * @author sunny
 *
 */
public class Surface extends ComplexObservable {

	/**
	 * The dyn4j dynamic objects
	 */
	protected DoubleProperty frictionCoef;
	protected Rectangle object;
	protected Body body;
	protected BodyFixture fixture;

	/**
	 * Position x,y and rotation in pixels and degrees for javaFx purpose
	 */
	private DoubleProperty topLeftCornerX, topLeftCornerY, rotation, height, width;

	/**
	 * Minimal constructor for a Surface. A surface is, by definition, set with
	 * an infinite mass.
	 * 
	 * @param width
	 *            The width of the surface in meters
	 * @param height
	 *            The height of the surface in meters
	 */

	public Surface(double width, double height) {
		super();

		// creates the object
		object = new Rectangle(width, height);

		// creates the fixture
		fixture = new BodyFixture(object);
		fixture.setRestitution(0);
		fixture.setFriction(DEFAULT_FRICTION);

		// creates the body
		body = new Body();
		body.setMass(MassType.INFINITE);
		body.addFixture(fixture);

		// Instantiate the frictionCoef listener
		this.frictionCoef = new SimpleDoubleProperty(DEFAULT_FRICTION);
		frictionCoef.addListener((value, old, newv) -> fixture.setFriction(newv.doubleValue()));

		// add body to the list
		bodies.add(body);

		topLeftCornerX = new SimpleDoubleProperty(0);
		topLeftCornerY = new SimpleDoubleProperty(0);
		this.rotation = new SimpleDoubleProperty(0);
		this.height = new SimpleDoubleProperty(ScaleManager.metersToPixels(height));
		this.width = new SimpleDoubleProperty(ScaleManager.metersToPixels(width));

	}

	/**********************************************************************
	 ********************** Getters and setters ***************************
	 *********************************************************************/

	/**
	 * The Surface's friction Coefficient property.
	 * 
	 * @return {@link #frictionCoef}
	 */
	public final DoubleProperty frictionCoefProperty() {
		return this.frictionCoef;
	}

	/**
	 * The Surface's friction Coefficient.
	 * 
	 * @return The value of {@link #frictionCoef}
	 */
	public final double getFrictionCoef() {
		return this.frictionCoefProperty().get();
	}

	/**
	 * Sets the surface's fricition coeficient.
	 * 
	 * @param frictionCoef
	 *            The new friction coeficient
	 */
	public final void setFrictionCoef(final double frictionCoef) {
		this.frictionCoefProperty().set(frictionCoef);
	}

	/**
	 * Updates the Surface's properties in order to keep the view proportional
	 * to the it's dimensions.
	 */
	@Override
	public void update() {
		topLeftCornerX
				.set(ScaleManager.metersToPixels((body.getTransform().getTranslationX() - object.getWidth() / 2)));
		topLeftCornerY
				.set(ScaleManager.metersToPixels((body.getTransform().getTranslationY() - object.getHeight() / 2)));
		rotation.set(Math.toDegrees(body.getTransform().getRotation()));

	}

	/**
	 * Makes the Surface rotate to the theta angle
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
	 * Makes the Surface translate to the specified position
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
	 * Javafx properties for view binding
	 * 
	 * @return The position x of the Surface
	 */
	public final ReadOnlyDoubleProperty topLeftCornerXProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.topLeftCornerX);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The position y of the Surface
	 */
	public final ReadOnlyDoubleProperty topLeftCornerYProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.topLeftCornerY);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The position y of the Surface
	 */
	public final ReadOnlyDoubleProperty widthProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.width);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The position y of the Surface
	 */
	public final ReadOnlyDoubleProperty heightProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.height);
	}

	/**
	 * Javafx properties for view binding
	 * 
	 * @return The rotation of the Surface
	 */
	public final ReadOnlyDoubleProperty rotationProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(this.rotation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getRotate() {
		return body.getTransform().getRotation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vector2 getTranslate() {
		return body.getTransform().getTranslation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void operateResize() {

		width.set(ScaleManager.metersToPixels(object.getWidth()));
		height.set(ScaleManager.metersToPixels(object.getHeight()));

	}

	/**
	 * Returns the surface's width in meter.
	 * 
	 * @return The surface's width in meter.
	 */
	public double getWidth() {
		return object.getWidth();
	}

	/**
	 * Returns the surface's height in meter.
	 * 
	 * @return The surface's height in meter.
	 */
	public double getHeight() {
		return object.getHeight();
	}

	/**
	 * Sets the width of the surface to the one in parameter.
	 * 
	 * @param width
	 *            The new width of the surface.
	 */
	public void setWidth(double width) {
		this.body.removeFixture(fixture);

		this.object = new Rectangle(width, getHeight());
		this.fixture = new BodyFixture(object);

		this.body.addFixture(fixture);

		operateResize();
	}
	/**
	 * Sets the height of the surface to the one in prameter.
	 * 
	 * @param width
	 *            The new height of the surface.
	 */
	public void setHeight(double height) {
		this.body.removeFixture(fixture);

		this.object = new Rectangle(getWidth(), height);
		this.fixture = new BodyFixture(object);

		this.body.addFixture(fixture);

		operateResize();

	}
}
