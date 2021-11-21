package gameObservables;

/**
 * Should normally be implemented by any Observable from the game that are Joint
 * appliers, which mean they apply joints on any object other then themselves.
 * Only used by the game to remove useless joints when an object needs to be
 * translated outside the game's field.
 * 
 * @author sunny
 *
 */
public interface JointApplier {

	/**
	 * This method removes the joints that the object applied to any
	 * object outside itself.
	 */
	public void removeAppliedJoint();
}
