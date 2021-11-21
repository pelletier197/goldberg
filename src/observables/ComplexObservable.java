package observables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

/**
 * <p>
 * A ComplexObservableObject is a simple adapter from the interface
 * {@link #AbstractComplexObservable}. It overrides a few methods:
 * {@link #applyForce(Vector2)},{@link #bodiesContains(Body)},
 * {@link #getBodies()},{@link #getJoints()},{@link #getRotate()},
 * {@link #getTranslate()},{@link #getView()},{@link #initializeObject(World)},
 * {@link #isOutside(Bounds)},{@link #translate(double, double)}.
 * </p>
 * 
 * <p>
 * All the objects extending this class should, most of the time, override those
 * methods by calling super() method.
 * 
 * This class contains specific type for
 * {@link #handleDynamicCollide(AbstractObservableObject, World)} and
 * {@link #handleStaticCollide(AbstractObservableObject, World)}, using
 * modifiable interface through the objects {@link #onStaticCollide} and
 * {@link #onDynamicCollide}.
 * </p>
 * 
 * @author Sunny Pelletier, Etienne Matteau, Mathieu Trudelle
 *
 */
public abstract class ComplexObservable implements AbstractComplexObservable {

	/**
	 * The object constants
	 */
	public static final double DEFAULT_FRICTION = BodyFixture.DEFAULT_FRICTION;
	public static final double NO_FRICTION = 0;
	public static final double INFINITE_FRICTION = Double.MAX_VALUE;
	public static final double HIGH_FRICTION = 0.8;
	public static final double MEDIUM_FRICTION = 0.5;

	/**
	 * The redefinition from the interface CollideEvent called when a collision
	 * happens on a static purpose. A static world is a world where collisions
	 * are detected, but where the objects seems like they are not moving
	 */
	protected CollideEvent onStaticCollide;
	/**
	 * The redefinition from the interface CollideEvent called when a collision
	 * happens on a dynamic purpose. A dynamic world is a world where collisions
	 * and moving are made on a real time scale.
	 */
	protected CollideEvent onDynamicCollide;

	/**
	 * The collide event holding the dynamic collide event ended
	 */
	protected CollideEvent onDynamicCollideEnds;

	/**
	 * The collide event holding the dynamic collide event ended
	 */
	protected CollideEvent onStaticCollideEnds;

	/**
	 * A list of Body. All the bodies that the object contains should
	 * necessarily be added to this list.
	 */
	protected List<Body> bodies;

	/**
	 * A list of joints. All the joints that the object apply to the world
	 * should necessarily be added to this list.
	 */
	protected List<Joint> joints;

	/**
	 * The boolean value telling if the object is crossedTeleportable
	 */
	protected boolean crossedTeleportable;

	/**
	 * Constructor of a ComplexObservable. By default, the object has
	 * {@link #onDynamicCollide} and {@link #onStaticCollide} values set to null
	 * value.
	 * 
	 * An object is also crossedTeleportable.
	 */
	public ComplexObservable() {
		bodies = new ArrayList<>();
		joints = new ArrayList<>();
		crossedTeleportable = true;

	}

	/**
	 * Called by the world to handle a dynamic collide.
	 * 
	 * Usually, a ContactListener in the dyn4j library is returns a ContactPoint
	 * to handle the collision. The best way to know if the body of this
	 * contactPoint is contained in the object is to call the
	 * {@link #bodiesContains(Body)} method.
	 */
	@Override
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {

		if (onDynamicCollide != null) {

			onDynamicCollide.handle(contactPoint, object, body, world);
		}

	}

	/**
	 * Called by the world to handle a static collide.
	 * 
	 * Usually, a ContactListener in the dyn4j library is returns a ContactPoint
	 * to handle the collision. The best way to know if the body of this
	 * contactPoint is contained in the object is to call the
	 * {@link #bodiesContains(Body)} method.
	 */
	@Override
	public void handleStaticCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world) {
		if (onStaticCollide != null) {
			onStaticCollide.handle(contactPoint, object, body, world);
		}

	}

	/**
	 * Sets the whole object as a sensor, which means it won't collide anymore
	 * but will detect collisions happening on it.
	 */
	@Override
	public void setSensor(boolean isSensor) {
		for (Body body : bodies) {

			Iterator<BodyFixture> fixtures = body.getFixtureIterator();

			while (fixtures.hasNext()) {
				fixtures.next().setSensor(isSensor);
			}

		}

	}

	/**
	 * Initialize an object in the world. Typically, this method only add all
	 * the bodies contained in {@link #bodies} to the world, and also all the
	 * joints contained in {@link #joints}.
	 * 
	 * This method will also set every body's user data as the current object.
	 * Therefore, the user data must never be changed, or the Dynamic world will
	 * occur in certain bugs.
	 */
	@Override
	public void initializeObject(World world) {

		if (world != null) {
			for (Body body : bodies) {
				world.addBody(body);
				body.setUserData(this);
			}
			for (Joint joint : joints)
				try {

					world.addJoint(joint);
				} catch (Exception e) {
					// If the joint is previously added
				}
		}

	}

	/**
	 * Apply the given force to all the objects contained in the {@link #bodies}
	 * . If the object are sticked together by joints, it may have not
	 * predictable behavior. This method is not recommended to be used without
	 * knowing what is the instance of the object.
	 * 
	 * @deprecated
	 */
	@Override
	public void applyForce(Vector2 force) {

		for (Body body : bodies) {

			body.applyForce(force);
		}

	}

	/**
	 * Returns the CollideEvent {@link #onStaticCollide()}
	 * 
	 * @return The CollideEvent {@link #onStaticCollide()}
	 */
	public CollideEvent getOnStaticCollide() {
		return onStaticCollide;
	}

	/**
	 * Sets the CollideEvent {@link #onStaticCollide}. When the method
	 * {@link #handleDynamicCollide(AbstractObservableObject, World)} will be
	 * called, the method
	 * {@link #CollideEvent.handleCollide(AbstractObservableObject, World)} from
	 * this interface will be called.
	 * 
	 * @param onStaticCollide
	 *            The new onStaticCollide event handling. Note : this parameter
	 *            can be null.
	 */
	public void setOnStaticCollide(CollideEvent onStaticCollide) {
		this.onStaticCollide = onStaticCollide;
	}

	/**
	 * Returns the CollideEvent {@link #onDynamicCollide}
	 * 
	 * @return The CollideEvent {@link #onDynamicCollide()}
	 */
	public CollideEvent getOnDynamicCollide() {
		return onDynamicCollide;
	}

	/**
	 * Returns the interface of this object used when
	 * {@link #handleDynamicCollideEnds(AbstractComplexObservable, Body, World)}
	 * is called.
	 * 
	 * @return The value of {@link #onDynamicCollideEnds)}
	 */
	public CollideEvent getOnDynamicCollideEnds() {
		return onDynamicCollideEnds;
	}

	/**
	 * Sets the value of {@link #onDynamicCollideEnds} to the one given in
	 * parameter. The handleCollide method of this object should normally be
	 * called every time a collision ends in the DynamicWorld.
	 * 
	 * @param onDynamicCollideEnds
	 *            The new value of {@link #onDynamicCollideEnds}
	 */
	public void setOnDynamicCollideEnds(CollideEvent onDynamicCollideEnds) {
		this.onDynamicCollideEnds = onDynamicCollideEnds;
	}

	/**
	 * Returns the interface of this object used when
	 * {@link #handleStaticCollideEnds(AbstractComplexObservable, Body, World)}
	 * is called.
	 * 
	 * @return The value of {@link #onStaticCollideEnds}
	 */
	public CollideEvent getOnStaticCollideEnds() {
		return onStaticCollideEnds;
	}

	/**
	 * Sets the value of {@link #onStaticCollideEnds} to the one given in
	 * parameter. The handleCollide method of this object should normally be
	 * called every time a collision ends in the DynamicWorld.
	 * 
	 * @param onDynamicCollideEnds
	 *            The new value of {@link #onStaticCollideEnds}
	 */
	public void setOnStaticCollideEnds(CollideEvent onStaticCollideEnds) {
		this.onStaticCollideEnds = onStaticCollideEnds;
	}

	/**
	 * Handle the collision with the given object, using the collideEvent
	 * {@link #onDynamicCollideEnds} handler. If the value of this object is
	 * null, no work is performed.
	 */
	@Override
	public void handleDynamicCollideEnds(Vector2 contactPoint, AbstractComplexObservable object, Body body,
			World world) {
		if (onDynamicCollideEnds != null) {
			onDynamicCollideEnds.handle(contactPoint, object, body, world);
		}

	}

	/**
	 * Handle the collision with the given object, using the collideEvent
	 * {@link #onStaticCollideEnds} handler. If the value of this object is
	 * null, no work is performed.
	 */
	@Override
	public void handleStaticCollideEnds(Vector2 contactPoint, AbstractComplexObservable object, Body body,
			World world) {
		if (onStaticCollideEnds != null) {
			onStaticCollideEnds.handle(contactPoint, object, body, world);
		}

	}

	/**
	 * Sets the CollideEvent {@link #onDynamicCollide}. When the method
	 * {@link #handleStaticCollide(AbstractObservableObject, World)} will be
	 * called, the method
	 * {@link #CollideEvent.handleCollide(AbstractObservableObject, World)} from
	 * this interface will be called.
	 * 
	 * @param onDynamicCollide
	 *            The new onDynamicCollide event handling. Note : this parameter
	 *            can be null.
	 */
	public void setOnDynamicCollide(CollideEvent onDynamicCollide) {
		this.onDynamicCollide = onDynamicCollide;
	}

	/**
	 * Tells if the body sent in parameters is contained in the list of bodies.
	 * If true, it would means that the body sent is part of the
	 * ComplexObservable itself.
	 * 
	 * @return true if the object is contained, false otherwise.
	 */
	@Override
	public boolean bodiesContains(Body body) {
		return bodies.contains(body);
	}

	/**
	 * Tells if all the bodies contained in the object are outside the bounds
	 * specified in parameter.
	 * 
	 * @return true if the the object's bodies are all contained outside the
	 *         bounds.
	 */
	@Override
	public boolean isOutside(Bounds bounds) {

		// Iterates over every body.
		for (Body body : bodies) {
			// If one of the body is inside the bounds, false is returned
			if (!bounds.isOutside(body)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the list of body this object contains. All those bodies are
	 * normally added to the world when the method
	 * {@link #initializeObject(World)} is called.
	 * 
	 * @returns the list of body this object contains.
	 */
	@Override
	public List<Body> getBodies() {

		return bodies;
	}

	/**
	 * Returns the list of joints this object contains. All those joints are
	 * normally added to the world when the method
	 * {@link #initializeObject(World)} is called.
	 * 
	 * @returns the list of joints this object contains.
	 */
	@Override
	public List<Joint> getJoints() {

		return joints;
	}

	/**
	 * Sets the object as the specified parameter of mobility.
	 * 
	 * <ul>
	 * In the case that mobile is false, the object will be completely stopped
	 * and all the bodies will be set as inactive bodies.
	 * <p>
	 * In the case that the mobile is true, the object will be set as an active
	 * and updated object.
	 */
	@Override
	public void setMobile(boolean mobile) {

		for (Body b : bodies) {
			if (!mobile) {
				b.setAngularVelocity(0);
				b.setLinearVelocity(0, 0);
				b.setGravityScale(0);
			} else {
				b.setActive(true);
				b.setAsleep(false);
				b.setGravityScale(1);
			}
		}
	}

	/**
	 * Sets the object as crossedTeleportable or not, depending on the fact that
	 * the object uses joints or not.
	 * 
	 * The object should handle this parameter by itself
	 */
	@Override
	public void setCrossedTeleportable(boolean value) {
		crossedTeleportable = value;

	}

	/**
	 * Tells either the object can be crossed teleportable, meaning they can be
	 * teleported by the {@link DynamicWorld}.
	 */
	@Override
	public boolean isCrossedTeleportable() {
		return crossedTeleportable;
	}

}
