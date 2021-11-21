package observables;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ScaleManager {

	/**
	 * The default scale.
	 */
	public static final double DEFFAULT_SCALE = 1;

	/**
	 * The default pixel per meter conversion used by
	 * {@link AbstractComplexObservable#update()} to perform property
	 * modification.
	 */
	public static final double DEFAULT_PIXELS_PER_METER = 45;
	/**
	 * The world scale.
	 */
	public static final DoubleProperty SCALE = new SimpleDoubleProperty(DEFFAULT_SCALE);

	/**
	 * The pixelPexMeter property
	 */
	public static final DoubleProperty PIXELS_PER_METER = new SimpleDoubleProperty(DEFAULT_PIXELS_PER_METER);

	/**
	 * The conversion method for converting meters to pixels depending on the
	 * Scale
	 * 
	 * @param meters
	 *            The value in meters
	 * 
	 * @return The value in pixels depending on the {@link #SCALE}
	 */
	public static double metersToPixels(double meters) {
		return meters * PIXELS_PER_METER.get();
	}

	/**
	 * The conversion method for converting pixels to meters depending on the
	 * Scale
	 * 
	 * @param pixels
	 *            The value in pixels
	 * 
	 * @return The value in meters depending on the {@link #SCALE}
	 */
	public static double pixelToMeters(double pixels) {
		return pixels / PIXELS_PER_METER.get();
	}

	/**
	 * Modify the scale of the application by updating making everyThing bigger.
	 * This may have a direct effect on
	 * {@link AbstractComplexObservable#update()} methods.
	 */
	public static void zoom() {
		SCALE.set(SCALE.get() + 0.005);
	}

	/**
	 * Modify the scale of the application by making everything smaller.This may
	 * have a direct effect on {@link AbstractComplexObservable#update()}
	 * methods.
	 */
	public static void unZoom() {
		if (SCALE.get() - 0.005 > 0) {
			SCALE.set(SCALE.get() - 0.005);
		}

	}

	/**
	 * Sets the application scale ratio for the one set in parameter.
	 * 
	 * A value of 1 represent a regular scale, and a value of 2 represents a
	 * doubled size.
	 * 
	 * @param scale
	 *            The new scale.
	 */
	public static void setScale(double scale) {
		SCALE.set(scale);
	}

	/**
	 * Reset the SCALE of the view to the default size, Which is 1.
	 */
	public static void reset() {
		SCALE.set(DEFFAULT_SCALE);
	}

}
