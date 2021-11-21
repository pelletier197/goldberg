package gameObservableViews;

import gameObservableControllers.ChildrenController;
import gameObservables.Observable;
import javafx.scene.Node;
import observables.AbstractComplexObservable;

public class ObservableWrapper {

	/**
	 * The object that represents the instance of the {@link #observable}
	 * object. Used for casting purpose.
	 */
	public Observable instance;

	/**
	 * The view's controller.
	 */
	public ChildrenController controller;
	
	/**
	 * This view is normally binded to the observable's properties which are
	 * updated by the method update from {@link #AbstractObservableObject}
	 */
	public Node view;

	/**
	 * The object itself. All it's properties are binded to the view.
	 */
	public AbstractComplexObservable observable;
}
