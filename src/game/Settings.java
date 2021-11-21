package game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gameObservableViews.ObservableObjectFactory;
import gameObservableViews.ObservableWrapper;
import gameObservables.Observable;
import observables.AbstractComplexObservable;

/**
 * Public class holding constants for creating {@link AbstractComplexObservable}
 * objects. Those variables should be used by the game to generate the objects
 * in the game.
 * 
 * @author Sunny, Etienne, Mathieu
 *
 */
public class Settings {

	public static final double DEFAULT_SPRING_WIDTH = 3;
	public static final double DEFAULT_SPRING_HEIGHT = 4;
	public static final double DEFAULT_SPRING_CONSTANT = 5;
	public static final double MAX_SPRING_CONSTANT = 100;
	public static final double MIN_SPRING_CONSTANT = 0;

	public static final double DEFAULT_SURFACE_WIDTH = 10;
	public static final double DEFAULT_SURFACE_HEIGHT = 1;
	public static final double MAX_SURACE_WIDTH = 50;
	public static final double MAX_SURFACE_HEIGHT = 50;
	public static final double MIN_SURACE_WIDTH = 0.5;
	public static final double MIN_SURFACE_HEIGHT = 0.5;

	public static final double DEFAULT_ROPE_LENGTH = 4;
	public static final double MAX_ROPE_LENGTH = 15;
	public static final double MIN_ROPE_LENGTH = 1;

	public static final double DEFAULT_COIN_RADIUS = 1.3;

	public static final double DEFAULT_POT_OF_GOLD_WIDTH = 2.9;
	public static final String PATH_TO_PERSONNAL = new File(".").getPath() + "/levels/personnal/";
	public static final String PATH_TO_DEFAULT = new File(".").getPath() + "/levels/campaign/";

	/**
	 * The file extension for the level files.
	 */
	public static final String EXTENSION = ".god";

	/**
	 * Generates a serial number for every level generated. This number is
	 * unique and might never be used a second time.
	 * 
	 * @return A randomly generated serial number.
	 */
	public static long generateSerialNumber() {
		return (long) (Math.random() * Long.MAX_VALUE);
	}

	/**
	 * The factory to generate default wrappers for
	 * {@link #getDefaultWrapper(Observable)}.
	 */
	private static final ObservableObjectFactory factory = new ObservableObjectFactory();

	/**
	 * Returns a default wrapper using static final variables contained in this
	 * class.
	 * 
	 * @param observable
	 *            The observable from which the wrapper has to be created from.
	 * @return A wrapper of the observable sent in parameter's instance.
	 */
	public static final ObservableWrapper getDefaultWrapper(Observable observable) {
		ObservableWrapper wrapper = null;

		switch (observable) {
		case SPRING:
			wrapper = factory.getWrapperInstance(Observable.SPRING, Settings.DEFAULT_SPRING_CONSTANT,
					Settings.DEFAULT_SPRING_HEIGHT, Settings.DEFAULT_SPRING_WIDTH);
			break;
		case BASCULE:
			wrapper = factory.getWrapperInstance(Observable.BASCULE, Settings.DEFAULT_SURFACE_WIDTH,
					Settings.DEFAULT_SURFACE_HEIGHT, 0);
			break;
		case POT_OF_GOLD:
			wrapper = factory.getWrapperInstance(Observable.POT_OF_GOLD, Settings.DEFAULT_POT_OF_GOLD_WIDTH,
					Settings.DEFAULT_POT_OF_GOLD_WIDTH, 0);
			break;
		case COIN:
			wrapper = factory.getWrapperInstance(Observable.COIN, Settings.DEFAULT_COIN_RADIUS, 0, 0);
			break;
		case STICKWALL:
			wrapper = factory.getWrapperInstance(Observable.STICKWALL, Settings.DEFAULT_SURFACE_WIDTH,
					Settings.DEFAULT_SURFACE_HEIGHT, 0);
			break;
		case ROPE:
			wrapper = factory.getWrapperInstance(Observable.ROPE, Settings.DEFAULT_ROPE_LENGTH, 0, 0);
			break;
		case SURFACE:
			wrapper = factory.getWrapperInstance(Observable.SURFACE, Settings.DEFAULT_SURFACE_WIDTH,
					Settings.DEFAULT_SURFACE_HEIGHT, 0);
			break;
		case STICK_BASCULE:
			wrapper = factory.getWrapperInstance(Observable.STICK_BASCULE, Settings.DEFAULT_SURFACE_WIDTH,
					Settings.DEFAULT_SURFACE_HEIGHT, 0);
			break;

		case DOMINO:
			wrapper = factory.getWrapperInstance(Observable.DOMINO, 0, 0, 0);
			break;
		}
		return wrapper;
	}

	/**
	 * Returns the list of planets that should be allowed to be used in the
	 * builder. This list will contain only the planets with their
	 * {@link Planet #isAllowedInBuilder()} value set to true.
	 * 
	 * Other planets are used for development purpose.
	 * 
	 * @return A list of planets with allowed planets for building worlds.
	 */
	public static final List<Planet> getBuildingPlanets() {

		final List<Planet> allowed = new ArrayList<>();

		for (Planet p : Planet.values()) {
			if (p.isAllowedInBuilder()) {
				allowed.add(p);
			}
		}

		return allowed;

	}

}
