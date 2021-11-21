package game;

import org.dyn4j.dynamics.Body;

import gameObservableViews.ObservableWrapper;
import gameObservables.Coin;
import gameObservables.PotOfGold;
import gameObservables.Rope;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import observables.AbstractComplexObservable;
import observables.ComplexObservable;

/**
 * Conventional class used by the {@link GoldbergGame} to apply the events of
 * the visuals of objects that are displayed in the wrappers. This class is used
 * to apply the Dynamic and Static events to the views.
 * 
 * @author 1434866
 *
 */
public class GameCollideHandling {

	/**
	 * The shadow applied to the Javafx Objects when they enter in static
	 * collide
	 */
	private DropShadow staticCollide;

	/**
	 * Constructor of the class.
	 */
	public GameCollideHandling() {

		staticCollide = new DropShadow(100, Color.RED);
		Light light = new Light.Distant(40, 40, Color.RED);
		Lighting lightning = new Lighting(light);
		staticCollide.setInput(lightning);
	}

	/**
	 * choose the correct Events for object
	 * 
	 * @param wrapper
	 *            object in collision
	 */
	public void applyEvents(ObservableWrapper wrapper) {

		final ComplexObservable observable = (ComplexObservable) wrapper.observable;

		/*
		 * Generates the dynamic collide on the object
		 */
		observable.setOnDynamicCollide((point, other, body, world) -> {

			// Play the sound associated to the objects colliding
			SoundMaker.playSound(getAssociatedSound(wrapper.observable, other, body));
		});

		/*
		 * Generates static collide
		 */
		observable.setOnStaticCollide((point, other, body, world) -> {
			if (observable != other && wrapper.view.getEffect() == null) {
				wrapper.view.setEffect(staticCollide);

			}

		});

		/*
		 * Generates static collide ends
		 */
		observable.setOnStaticCollideEnds((point, other, body, world) -> {
			if (observable != other && wrapper.view.getEffect() != null) {
				wrapper.view.setEffect(null);
			}

		});
	}

	/**
	 * return the sound associated with objects in collisions
	 * 
	 * @param object
	 *            first object in collision
	 * @param other
	 *            second object in collision
	 * @param body
	 *            body of first object
	 * @return correct sound
	 */
	private GameSound getAssociatedSound(AbstractComplexObservable object, AbstractComplexObservable other, Body body) {

		GameSound sound = null;

		if (isMetalToMetalCollide(object, other)) {
			sound = GameSound.METAL_TO_METAL;

		} else if (isRopeMagnetEvent(object, other, body)) {
			sound = GameSound.MAGNET;
		}

		return sound;
	}

	/**
	 * return if is a metal and metal collide
	 * 
	 * @param object
	 *            first object in collision
	 * @param other
	 *            second object in collision
	 * @return true if the first and second object is metal false if is not
	 *         metal
	 */
	private boolean isMetalToMetalCollide(AbstractComplexObservable object, AbstractComplexObservable other) {

		boolean isMetal = (object instanceof Coin && other instanceof PotOfGold)
				|| (object instanceof PotOfGold && other instanceof Coin);

		return isMetal;
	}

	/**
	 * return if is a magnet is in collision
	 * 
	 * @param object
	 *            first Object in collision
	 * @param other
	 *            second object in collision
	 * @param body
	 *            body of the first object in collision
	 * @return true if object in collision is magnet of Rope false if is not
	 */
	private boolean isRopeMagnetEvent(AbstractComplexObservable object, AbstractComplexObservable other, Body body) {

		Rope rope = null;
		if (object instanceof Rope && other instanceof Coin) {
			rope = (Rope) object;
		} else if (other instanceof Rope && object instanceof Coin) {
			rope = (Rope) other;
		} else {
			return false;
		}

		return rope.isMagnet(body) && rope.canCatch();
	}
}
