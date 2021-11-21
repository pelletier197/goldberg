package observables;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

public interface CollideEvent {

	/**
	 * Used to specify an action that has to be handled when a collision event
	 * is detected.
	 * 
	 * @param object
	 *            The other object that entered in collision. Note that this
	 *            object can possibly be null.
	 * @param world
	 *            The world in which the event occurred.
	 * 
	 * @param body
	 *            The body of the current object that entered in collision. This
	 *            object can be null.
	 */
	public void handle(Vector2 contactPoint, AbstractComplexObservable object, Body body, World world);

}
