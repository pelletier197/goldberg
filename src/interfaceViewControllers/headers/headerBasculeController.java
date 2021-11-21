package interfaceViewControllers.headers;

import java.net.URL;
import java.util.ResourceBundle;

import game.Settings;
import gameObservables.Bascule;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.NumberField;
import observables.AbstractComplexObservable;

public class headerBasculeController implements Header, Initializable {
	@FXML
	private NumberField width;
	@FXML
	private NumberField height;
	@FXML
	private NumberField mass;

	/**
	 * The object itself
	 */
	private Bascule bascule;

	/**
	 * The change listeners on textFields
	 */
	private ChangeListener<Number> widthL, heightL, massL;

	/**
	 * Sets the observable of the header. The parameters of the textfield will
	 * be charged to the object's important parameters.
	 */
	@Override
	public void setObservable(AbstractComplexObservable object) {
		if (object instanceof Bascule) {
			this.bascule = (Bascule) object;
			this.width.setValue(bascule.getWidth());
			this.height.setValue(bascule.getHeight());
			this.mass.setValue(bascule.getMass());

			/*
			 * Removes old listeners
			 */
			if (widthL != null) {
				this.width.valueProperty().removeListener(widthL);
				this.height.valueProperty().removeListener(heightL);
				this.mass.valueProperty().removeListener(massL);
			}

			/*
			 * Creates the listeners for new value
			 */
			this.widthL = ((value, old, newv) -> {
				bascule.setWidth(newv.doubleValue());
			});
			this.heightL = ((value, old, newv) -> {
				bascule.setHeight(newv.doubleValue());
			});
			this.massL = ((value, old, newv) -> {
				this.bascule.setMass(newv.doubleValue());
			});

			/*
			 * add the new listeners
			 */
			this.width.valueProperty().addListener(widthL);
			this.height.valueProperty().addListener(heightL);
			this.mass.valueProperty().addListener(massL);
		} else {
			throw new IllegalArgumentException("Cannot operate with " + object.getClass());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unBind() {
		if (widthL != null) {
			this.width.valueProperty().removeListener(widthL);
			this.height.valueProperty().removeListener(heightL);
			this.mass.valueProperty().removeListener(massL);
		}
	}

	/**
	 * Initializes the numberfield's restriction matching the Settings
	 * parameter.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.height.setMax(Settings.MAX_SURFACE_HEIGHT);
		this.height.setMin(Settings.MIN_SURFACE_HEIGHT);
		this.width.setMax(Settings.MAX_SURACE_WIDTH);
		this.width.setMin(Settings.MIN_SURACE_WIDTH);
		this.mass.setMin(0.00000001);
		this.mass.setMax(Double.MAX_VALUE);

	}

}
