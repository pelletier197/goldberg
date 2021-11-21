package observables;

import org.dyn4j.dynamics.Body;

/**
 * Defines a pair of {@link AbstractComplexObservable} that entered in
 * collision. The object in collision are kept together by this object.
 * 
 * It can be used to detect the end of the collisions using
 * {@link #stillCollides()} if the world is not able to do it by itself.
 * 
 * @author sunny
 *
 */
public class CollidingPair implements Comparable<CollidingPair> {

	/**
	 * The first object of the pair
	 */
	public AbstractComplexObservable obj1;
	/**
	 * The second object of the pair
	 */
	public AbstractComplexObservable obj2;

	/**
	 * Constructs the pair with the 2 given objects.
	 * 
	 * @param obj1
	 *            The first object
	 * @param obj2
	 *            The second object
	 */
	public CollidingPair(AbstractComplexObservable obj1, AbstractComplexObservable obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	/**
	 * Iterates over every body to tell if the pair still collides.
	 * 
	 * @return True if the object are still in contact, false otherwise.
	 */
	public boolean stillCollides() {
		for (Body b : obj1.getBodies()) {
			for (Body b2 : obj2.getBodies()) {
				if (b.isInContact(b2)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Tells either the pair contains the object in parameter.
	 * 
	 * @param object
	 *            The object that have to be verified it is contained or not in
	 *            the pair.
	 * @return True if the pair contains the object, false otherwise.
	 */
	public boolean contains(AbstractComplexObservable object) {
		return object == obj1 || object == obj2;
	}

	/**
	 * Comparison of the 2 objects using the hashcode. Simply used to sort them
	 * in a collection and to remove them easily from collections.
	 */
	@Override
	public int compareTo(CollidingPair o) {
		return this.obj1.hashCode() + this.obj2.hashCode();
	}
}
