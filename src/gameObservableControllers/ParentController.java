package gameObservableControllers;

import gameObservables.Observable;
import javafx.scene.input.MouseEvent;
import observables.AbstractComplexObservable;

/**
 * The interface implemented by big views containing small children that have to
 * notify the parent that it has been clicked. Normally, children will notify
 * the parent via
 * {@link #notifyChildrenPressed(AbstractComplexObservable, Observable, ChildrenController, MouseEvent)}
 * , and parents will be able to handle the click, knowing which children has
 * been clicked.
 * 
 * @author sunny
 *
 */
public interface ParentController {

	/**
	 * Notifies the parent controller that the view of the object in parameter
	 * has been pressed. This method also send the source Controller of this
	 * call.
	 * 
	 * @param object
	 *            The object that has been pressed in the view.
	 * @param instance
	 *            The object's instance
	 * @param source
	 *            The source controller of this callback
	 */
	public void notifyChildrenPressed(AbstractComplexObservable object, Observable instance, ChildrenController source,
			MouseEvent event);

}
