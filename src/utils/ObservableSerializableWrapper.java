package utils;

import java.io.Serializable;

import org.dyn4j.geometry.Vector2;

import gameObservables.Bascule;
import gameObservables.Coin;
import gameObservables.Domino;
import gameObservables.Observable;
import gameObservables.PotOfGold;
import gameObservables.Rope;
import gameObservables.Spring;
import gameObservables.StickBascule;
import gameObservables.StickWall;
import observables.AbstractComplexObservable;
import observables.Surface;

public class ObservableSerializableWrapper implements Serializable {

	/**
	 * The serial version UID of this object
	 */
	private static final long serialVersionUID = 1809256496883672785L;

	/**
	 * The instance of the object
	 */
	protected Observable instance;

	/**
	 * The position X and Y of object The rotation of object
	 */
	protected double posX, posY;
	protected double rotation;

	/**
	 * The parameters depend on what is the object's instance
	 */
	protected double param1, param2, param3;

	/**
	 * 
	 * This method sets all parameter of object depending on what is its
	 * instance
	 *
	 * @param instance
	 *            The instance of object
	 * @param object
	 *            Serializable object
	 */
	public void setObservable(Observable instance, AbstractComplexObservable object) {
		if (object == null) {
			throw new NullPointerException();
		} else {
			savePosition(object);

			switch (instance) {
			case BASCULE:
				setObservable((Bascule) object);
				break;
			case COIN:
				setObservable((Coin) object);
				break;
			case POT_OF_GOLD:
				setObservable((PotOfGold) object);
				break;
			case ROPE:
				setObservable((Rope) object);
				break;
			case SPRING:
				setObservable((Spring) object);
				break;
			case STICKWALL:
				setObservable((StickWall) object);
				break;
			case SURFACE:
				setObservable((Surface) object);
				break;
			case STICK_BASCULE:
				setObservable((StickBascule) object);
				break;

			case DOMINO:
				this.instance = Observable.DOMINO;
				break;

			}
		}
	}

	/**
	 * This method sets all the parameters of the object depending on what is
	 * its instance
	 * 
	 * @param object
	 *            Serializable object
	 */
	private void setObservable(StickBascule object) {
		setObservable((Bascule) object);
		this.instance = Observable.STICK_BASCULE;

	}

	/**
	 * This method sets all the parameters of the object depending on what is
	 * its instance
	 * 
	 * @param object
	 *            Serializable object
	 */
	public void setObservable(AbstractComplexObservable object) {

		if (object == null) {
			throw new NullPointerException();
		} else {
			// First save general positions
			savePosition(object);

			if (object.getClass() == Bascule.class) {
				setObservable((Bascule) object);
			} else if (object.getClass() == Coin.class) {
				setObservable((Coin) object);
			} else if (object.getClass() == PotOfGold.class) {
				setObservable((PotOfGold) object);
			} else if (object.getClass() == Rope.class) {
				setObservable((Rope) object);
			} else if (object.getClass() == Spring.class) {
				setObservable((Spring) object);
			} else if (object.getClass() == StickWall.class) {
				setObservable((StickWall) object);
			} else if (object.getClass() == Surface.class) {
				setObservable((Surface) object);
			} else if (object.getClass() == StickBascule.class) {
				setObservable((StickBascule) object);
			} else if (object.getClass() == Domino.class) {
				this.instance = Observable.DOMINO;
			}

		}
	}

	/**
	 * Set the position X and Y and rotation
	 * 
	 * @param object
	 *            Serializable Object
	 */
	private void savePosition(AbstractComplexObservable object) {
		Vector2 pos = object.getTranslate();
		this.posX = pos.x;
		this.posY = pos.y;
		this.rotation = object.getRotate();
	}

	/**
	 * set the instance of Object SpringConstance equals param1 Height equals
	 * param2 Width equals param3
	 * 
	 * @param object
	 *            Serializable Spring
	 */
	private void setObservable(Spring object) {
		instance = Observable.SPRING;
		this.param1 = object.getSpringConstant().get();
		this.param2 = object.getHeight();
		this.param3 = object.getWidth();

	}

	/**
	 * set the instance of Object RopeHeight equals param1 0 equals param2 0
	 * equals param3
	 * 
	 * @param object
	 *            Serializable Rope
	 */
	private void setObservable(Rope object) {
		instance = Observable.ROPE;
		this.param1 = object.getRopeHeight();
		this.param2 = 0;
		this.param3 = 0;
	}

	/**
	 * set the instance of Object Width equals param1 Height equals param2 0
	 * equals param3
	 * 
	 * @param object
	 *            Serializable Bascule
	 */
	private void setObservable(Bascule object) {
		setObservable((Surface) object);
		instance = Observable.BASCULE;
		this.param3 = object.getMass();
	}

	/**
	 * set the instance of Object Width equals param1 Height equals param2 0
	 * equals param3
	 * 
	 * @param object
	 *            Serializable Surface
	 */
	private void setObservable(Surface object) {
		instance = Observable.SURFACE;
		this.param1 = object.getWidth();
		this.param2 = object.getHeight();
		this.param3 = 0;
	}

	/**
	 * set the instance of Object Width equals param1 Height equals param2 0
	 * equals param3
	 * 
	 * @param object
	 *            Serializable StickWall
	 */
	private void setObservable(StickWall object) {
		setObservable((Surface) object);
		instance = Observable.STICKWALL;
	}

	/**
	 * set the instance of Object Radius equals param1 0 equals param2 0 equals
	 * param3
	 * 
	 * @param object
	 *            Serializable Coin
	 */
	private void setObservable(Coin object) {
		instance = Observable.COIN;
		this.param1 = object.getRadius();
		this.param2 = 0;
		this.param3 = 0;
	}

	/**
	 * set the instance of Object Width equals param1 Height equals param2 0
	 * equals param3
	 * 
	 * @param object
	 *            Serializable PotOfGold
	 */
	private void setObservable(PotOfGold object) {
		setObservable((Surface) object);
		instance = Observable.POT_OF_GOLD;
	}

}
