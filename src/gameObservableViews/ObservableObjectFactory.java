package gameObservableViews;

import java.io.IOException;

import gameObservableControllers.BasculeController;
import gameObservableControllers.CoinController;
import gameObservableControllers.DominoController;
import gameObservableControllers.PotOfGoldController;
import gameObservableControllers.RopeController;
import gameObservableControllers.SpringController;
import gameObservableControllers.StickBasculeController;
import gameObservableControllers.StickWallController;
import gameObservableControllers.SurfaceController;
import gameObservables.Bascule;
import gameObservables.Coin;
import gameObservables.Domino;
import gameObservables.Observable;
import gameObservables.PotOfGold;
import gameObservables.Rope;
import gameObservables.Spring;
import gameObservables.StickBascule;
import gameObservables.StickWall;
import javafx.fxml.FXMLLoader;
import observables.AbstractComplexObservable;
import observables.Surface;

/**
 * Simple factory used to generate the observable wrappers of the objects of the
 * game.
 * 
 * Use {@link #getWrapperInstance(AbstractComplexObservable)} to get a wrapper
 * from the object's instance binded to the view's properties.
 * 
 * Either, use {@link #getWrapperInstance(Observable, double, double, double)}
 * to create a full wrapper of the object contained in parameter matching the
 * parameters. However, make sure the parameters match the restriction specified
 * in the documentation.
 * 
 * @author Sunny, Mathieu
 *
 */
public class ObservableObjectFactory {

	public static final String SPRING_FXML_PATH = "/gameObservableViews/Spring.fxml";
	public static final String ROPE_FXML_PATH = "/gameObservableViews/Rope.fxml";
	public static final String BASCULE_FXML_PATH = "/gameObservableViews/Bascule.fxml";
	public static final String POT_OF_GOLD_FXML_PATH = "/gameObservableViews/Pot_Of_Gold.fxml";
	public static final String STICKWALL_FXML_PATH = "/gameObservableViews/StickWall.fxml";
	public static final String SURFACE_FXML_PATH = "/gameObservableViews/Surface.fxml";
	public static final String COIN_FXML_PATH = "/gameObservableViews/Coin.fxml";
	public static final String STICK_BASCULE_FXML_PATH = "/gameObservableViews/StickBascule.fxml";
	public static final String DOMINO_FXML = "/gameObservableViews/Domino.fxml";

	/**
	 * This method is used to get an instance of the object specified in
	 * parameter returned as an ObservableWrapper. This wrapper will contain
	 * everything required to ensure that the view and its associated object
	 * remain together. Note also that the view contained in the wrapper after
	 * this method is called is binded to the observable of the wrapper.
	 * <p>
	 * 
	 * <p>
	 * Note that many types of Exception will be thrown if the parameters do not
	 * match the following characteristics.
	 * 
	 * <p>
	 * If observable == SPRING
	 * <ul>
	 * <li><b>param1</b> : The spring constant of the spring. Must be greater
	 * than 0</li>
	 * <li><b>param2</b> : The height of the spring. Must be greater than 0</li>
	 * <li><b>param3</b> : The width of the spring. Must be greater than 0</li>
	 * </ul>
	 * 
	 * <p>
	 * If observable == BASCULE || observable == POT_OF_GOLD || observable ==
	 * STICKWALL || observable == SURFACE || STICK_BASCULE
	 * <ul>
	 * <li><b>param1</b> : The width of the Object. Must be greater than 0.</li>
	 * <li><b>param2</b> : The height of the Object. Must be greater than 0.
	 * </li>
	 * <li><b>param3</b> : The mass for the bascule, not used for others.</li>
	 * </ul>
	 * 
	 * <p>
	 * If observable == ROPE
	 * <ul>
	 * <li><b>param1</b> : The length of the rope. Must be greater than 0.</li>
	 * <li><b>param2</b> : Not used. Can have any value.</li>
	 * <li><b>param3</b> : Not used. Can have any value.</li>
	 * </ul>
	 * 
	 * <p>
	 * If observable == COIN
	 * <ul>
	 * <li><b>param1</b> : The radius of the Coin. Must be greater than 0.</li>
	 * <li><b>param2</b> : Not used. Can have any value.</li>
	 * <li><b>param3</b> : Not used. Can have any value.</li>
	 * </ul>
	 * 
	 * 
	 * @param observable
	 *            The observable desired. The parameters must match its
	 *            restrictions.
	 * @return A wrapper containing the instance of the object, its view, its
	 *         controller and the object itself, or null if observable is Null.
	 * 
	 */
	public ObservableWrapper getWrapperInstance(Observable observable, double param1, double param2, double param3) {
		ObservableWrapper wrapper = null;
System.out.println(observable);
		switch (observable) {
		case SPRING:
			wrapper = createWrapper(SPRING_FXML_PATH, observable, new Spring(param1, param2, param3));
			((SpringController) wrapper.controller).setSpring((Spring) wrapper.observable);
			break;
		case BASCULE:
			wrapper = createWrapper(BASCULE_FXML_PATH, observable, new Bascule(param1, param2, param3));
			((BasculeController) wrapper.controller).setBascule((Bascule) wrapper.observable);
			break;
		case POT_OF_GOLD:
			wrapper = createWrapper(POT_OF_GOLD_FXML_PATH, observable, new PotOfGold(param1, param2));
			((PotOfGoldController) wrapper.controller).setPotOfGold((PotOfGold) wrapper.observable);
			break;
		case COIN:
			wrapper = createWrapper(COIN_FXML_PATH, observable, new Coin(param1, param2, param3));
			((CoinController) wrapper.controller).setCoin((Coin) wrapper.observable);
			break;
		case STICKWALL:
			wrapper = createWrapper(STICKWALL_FXML_PATH, observable, new StickWall(param1, param2));
			((StickWallController) wrapper.controller).setStickWall((StickWall) wrapper.observable);
			break;
		case ROPE:
			wrapper = createWrapper(ROPE_FXML_PATH, observable, new Rope(param1));
			((RopeController) wrapper.controller).setRope((Rope) wrapper.observable);
			break;
		case SURFACE:

			wrapper = createWrapper(SURFACE_FXML_PATH, observable, new Surface(param1, param2));

			((SurfaceController) wrapper.controller).setSurface((Surface) wrapper.observable);
			break;

		case DOMINO:
			wrapper = createWrapper(DOMINO_FXML, observable, new Domino());
			((DominoController) wrapper.controller).setDomino((Domino) wrapper.observable);
			
			break;

		case STICK_BASCULE:
			wrapper = createWrapper(STICK_BASCULE_FXML_PATH, observable, new StickBascule(param1, param2));
			((StickBasculeController) wrapper.controller).setBascule((Bascule) wrapper.observable);
			break;

		}
		return wrapper;

	}

	/**
	 * This method is used to get an instance of the object specified in
	 * parameter returned as an ObservableWrapper. This wrapper will contain
	 * everything required to ensure that the view and its associated object
	 * remain together. Note also that the view contained in the wrapper after
	 * this method is called is binded to the observable of the wrapper.
	 * 
	 * 
	 * @param object
	 *            The object for the wrapper
	 * @return A wrapper containing the instance of the object, its view, its
	 *         controller and the object itself, or null if observable is Null.
	 */
	public ObservableWrapper getWrapperInstance(AbstractComplexObservable object) {
		ObservableWrapper wrapper = null;

		if (object instanceof Spring) {

			wrapper = createWrapper(SPRING_FXML_PATH, Observable.SPRING, object);
			((SpringController) wrapper.controller).setSpring((Spring) wrapper.observable);

		} else if (object instanceof StickBascule) {

			wrapper = createWrapper(STICK_BASCULE_FXML_PATH, Observable.STICK_BASCULE, object);
			((StickBasculeController) wrapper.controller).setBascule((Bascule) wrapper.observable);

		}  else if (object instanceof Domino) {

			wrapper = createWrapper(DOMINO_FXML, Observable.DOMINO, object);
			((DominoController) wrapper.controller).setDomino((Domino) wrapper.observable);

		}else if (object instanceof PotOfGold) {

			wrapper = createWrapper(POT_OF_GOLD_FXML_PATH, Observable.POT_OF_GOLD, object);
			((PotOfGoldController) wrapper.controller).setPotOfGold((PotOfGold) wrapper.observable);

		} else if (object instanceof Coin) {

			wrapper = createWrapper(COIN_FXML_PATH, Observable.COIN, object);
			((CoinController) wrapper.controller).setCoin((Coin) wrapper.observable);

		} else if (object instanceof StickWall) {

			wrapper = createWrapper(STICKWALL_FXML_PATH, Observable.STICKWALL, object);
			((StickWallController) wrapper.controller).setStickWall((StickWall) wrapper.observable);

		} else if (object instanceof Rope) {

			wrapper = createWrapper(ROPE_FXML_PATH, Observable.ROPE, object);
			((RopeController) wrapper.controller).setRope((Rope) wrapper.observable);
		} else if (object instanceof Bascule) {

			wrapper = createWrapper(BASCULE_FXML_PATH, Observable.BASCULE, object);
			((BasculeController) wrapper.controller).setBascule((Bascule) wrapper.observable);

		} else if (object instanceof Surface) {

			wrapper = createWrapper(SURFACE_FXML_PATH, Observable.SURFACE, object);
			((SurfaceController) wrapper.controller).setSurface((Surface) wrapper.observable);

		}

		return wrapper;
	}

	/**
	 * This method creates wrapper of object
	 * 
	 * @param fxmlPath
	 *            FXML of the object
	 * @param observable
	 *            The observable desired. The parameters must match its
	 *            restrictions.
	 * @param object
	 *            the object for the wrapper
	 * @return A wrapper containing the instance of the object, its view, its
	 *         controller and the object itself, or null if observable is Null.
	 */
	private ObservableWrapper createWrapper(String fxmlPath, Observable observable, AbstractComplexObservable object) {
		// Creates an empty wrapper
		ObservableWrapper wrapper = new ObservableWrapper();

		// Loads everything from the files, and put attributes into the wrapper
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

		try {

			wrapper.view = loader.load();

		} catch (IOException e) {
			// Should never occur if the paths are well defined.
		}
		wrapper.controller = loader.getController();
		wrapper.instance = observable;
		wrapper.observable = object;

		return wrapper;
	}
}
