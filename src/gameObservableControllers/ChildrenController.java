package gameObservableControllers;

/**
 * This interface should be implemented by any Observable's view, and is used to
 * set the parent controller of the children. Children will notify parents via
 * {@link ParentController #notifyChildrenPressed(observables.AbstractComplexObservable, gameObservables.Observable, ChildrenController, javafx.scene.input.MouseEvent)}
 * 
 * @author Sunny
 *
 */
public interface ChildrenController {

	/**
	 * Sets the parent Controller of the children. Typically, a view clicked or
	 * dragged should notify the parentController via
	 * {@link ParentController #notifyChildrenPressed(observables.AbstractComplexObservable, gameObservables.Observable, ChildrenController, javafx.scene.input.MouseEvent)}
	 * method.
	 * 
	 * @param parent
	 *            The parent controller set to this children.
	 */
	public void setParentController(ParentController parent);

}
