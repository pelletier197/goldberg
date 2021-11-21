package interfaceViewControllers.headers;

import observables.AbstractComplexObservable;

/**
 * The interface that every header from the game should implement.
 * 
 * @author Sunny, Etienne
 *
 */
public interface Header {

	/**
	 * Sets the header's observable.
	 * 
	 * @throws Exception
	 *             if the object doesn't match the header's object restriction.
	 * @param object
	 *            The object itself.
	 */
	public void setObservable(AbstractComplexObservable object);

	/**
	 * Unbinds the header's properties.
	 */
	public void unBind();
}
