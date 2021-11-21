package gameObservables;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;

import observables.Surface;

public class Domino extends Surface {

	public static final double HEIGHT = 3;
	public static final double WIDTH = 0.5;

	public Domino() {
		super(WIDTH, HEIGHT);
		super.body.setMass(new Mass(super.body.getLocalCenter(), 0.001, 0.001));
	}

}
