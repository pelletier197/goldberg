package interfaceViewControllers.headers;

import java.net.URL;
import java.util.ResourceBundle;

import game.Settings;
import gameObservables.Spring;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import main.NumberField;
import observables.AbstractComplexObservable;

public class headerSpringController implements Header, Initializable {
	@FXML
	private NumberField springConstant;

	@FXML
	private CheckBox detonateur;
	
	/**
	 * The change listeners on textFields
	 */
	private ChangeListener<Number> springConstantL;
	
	/**
	 * The change listener on the checkbox
	 */
	private ChangeListener<Boolean> detonatorL;
	
	/**
	 * The object itself
	 */
	private Spring spring;

	/**
	 * Sets the observable of the header. The parameters of the textfield will
	 * be charged to the object's important parameters.
	 */
	@Override
	public void setObservable(AbstractComplexObservable object) {
		if (object instanceof Spring) {
			this.spring = (Spring) object;
			this.springConstant.setValue(spring.getSpringConstant().get());
			this.detonateur.setSelected(spring.isDetonator());

			/*
			 * Removes old listeners
			 */
			if (springConstantL != null) {
				this.springConstant.valueProperty().removeListener(springConstantL);
				this.detonateur.selectedProperty().removeListener(detonatorL);
			}

			/*
			 * Creates the listeners for new value
			 */
			this.springConstantL = ((value, old, newv) -> {
				spring.setSpringConstant(newv.doubleValue());
			});

			this.detonatorL = ((value, old, newv) -> {
				spring.setDetonator(newv);
			});

			/*
			 * add the new listeners
			 */
			this.springConstant.valueProperty().addListener(springConstantL);
			this.detonateur.selectedProperty().addListener(detonatorL);

		} else {
			throw new IllegalArgumentException("Cannot operate with " + object.getClass());
		}

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unBind() {
		if (springConstantL != null) {
			this.springConstant.valueProperty().removeListener(springConstantL);
			this.detonateur.selectedProperty().removeListener(detonatorL);

		}
	}
	/**
	 * Initializes the numberfield's restriction matching the Settings
	 * parameter.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.springConstant.setMax(Settings.MAX_SPRING_CONSTANT);
		this.springConstant.setMin(Settings.MIN_SPRING_CONSTANT);
	}

}
