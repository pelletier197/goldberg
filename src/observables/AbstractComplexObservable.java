package observables;

import java.util.List;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

public interface AbstractComplexObservable {

	/**
	 * Called to ensure that the object is initialized in the world. The object
	 * should initialized itself in the world, including placing joints /
	 * listeners if required.
	 * 
	 * @param world
	 *            The world which the object is added to
	 */
	public void initializeObject(World world);

	/**
	 * Rotate the object from a theta angle. Many complex objects have to rotate
	 * on a personalized way, so they have to handle the rotation.
	 * 
	 * @param theta
	 *            The angle of rotation in radians
	 */
	public void rotate(double theta);

	/**
	 * Translate the object from a x and y position. Many complex objects have
	 * to translate on a personalized way, so they have to handle this event.
	 * 
	 * Note that the object must translate to the given position, and not add
	 * those value to the actual translation
	 * 
	 * @param x
	 *            The new position x in meters
	 * 
	 * @param y
	 *            The new position y in meters
	 */
	public void translate(double x, double y);

	/**
	 * Apply a force to the given object. The object can choose if it will apply
	 * the force on all it's bodies or to a single one.
	 * 
	 * @param force
	 *            The force vector.
	 */
	public void applyForce(Vector2 force);

	/**
	 * Iterates over the object's bodies to know if the body sent in parameters
	 * is contained in the object. Used to know if the object is part of a
	 * collision or not during the update. The call of {@link #update()} method
	 * will depend of the value returned by this method.
	 * 
	 * @param body
	 *            The body that might be contained in the object.
	 * 
	 * @return true if the body is contained in the object, false otherwise.
	 */
	public boolean bodiesContains(Body body);

	/**
	 * Returns the rotate value of the object in radians
	 * 
	 * @return Returns the rotate value of the object
	 */
	public double getRotate();

	/**
	 * Returns the translate vector of the object in meters
	 * 
	 * @return Returns the translate vector of the object in meters
	 */
	public Vector2 getTranslate();

	/**
	 * This method is used to calculate if the object is completely outside the
	 * world's bounds.
	 * 
	 * @return true if the object is fully contained outside the bounds, false
	 *         otherwise.
	 */
	public boolean isOutside(Bounds bounds);

	/**
	 * Returns all the bodies contained in this complexObject
	 * 
	 * @return All the bodies contained in this complexObject
	 */
	public List<Body> getBodies();

	/**
	 * Returns all the joints contained in this complexObject
	 * 
	 * @return All the bodies contained in this complexObject
	 */
	public List<Joint> getJoints();

	/**
	 * Stops the actual movement of the object if the value is false, or makes
	 * it start if the value is true.
	 */
	public void setMobile(boolean mobile);

	/**
	 * Called when the object collides with another object on a dynamic view.
	 * (The object moves and react at a normal speed)
	 * 
	 * @param object
	 *            The object it collided with
	 * @param world
	 *            The world in which the collision occurred
	 */
	public void handleDynamicCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world);

	/**
	 * Called when the object collides with another object on a static view.
	 * (The object does not move or react to collision)
	 * 
	 * @param object
	 *            The object it collided with
	 * @param world
	 *            The world in which the collision occurred
	 */
	public void handleStaticCollide(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world);

	/**
	 * Handle the end of the collision with the given observable object. This
	 * should be called by the world when set to dynamic value.
	 * 
	 * @param object
	 *            The object with which the collide ended
	 * @param body
	 *            The body of the current object with which the collide ended
	 * @param world
	 *            The world in which collision occurred
	 */
	public void handleDynamicCollideEnds(Vector2 contactPoint, AbstractComplexObservable object, Body body,
			World world);

	/**
	 * Handle the end of a static collision between 2 objects in the world. This
	 * will also be called when world will be set to dynamic true.
	 * 
	 * @param object
	 *            The object that was in collision
	 * @param body
	 *            The body of the current object that was in collision
	 * @param world
	 *            The world in which collision ended.
	 */
	public void handleStaticCollideEnds(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world);

	/**
	 * Update the object's properties in order to keep the javaFx view's
	 * position proportional to the physic model.
	 * 
	 * NOTE : This method should be the only one to changed between platforms.
	 */
	public void update();

	/**
	 * Called every time an object must be resized, in order that it resize it's
	 * view binded properties.
	 * 
	 * @deprecated This method should never be called since the most recent
	 *             version.
	 */
	@Deprecated
	public void operateResize();

	/**
	 * Sets the object as a sensor. A sensor detects collision but is never
	 * involved into collisions.
	 * 
	 * @param isSensor
	 */
	public void setSensor(boolean isSensor);

	/**
	 * Sets the object as crossedTeleportable or not depending on the fact that
	 * joints are applied to it or not.
	 */
	public void setCrossedTeleportable(boolean value);

	/**
	 * Returns the value depending on which an object is crossed teleportable or
	 * not.
	 */
	public boolean isCrossedTeleportable();

}
