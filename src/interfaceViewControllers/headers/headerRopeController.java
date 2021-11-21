package interfaceViewControllers.headers;

import java.net.URL;
import java.util.ResourceBundle;

import game.Settings;
import gameObservables.Rope;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.NumberField;
import observables.AbstractComplexObservable;

public class headerRopeController implements Header, Initializable {
	@FXML
	private NumberField height;
	/**
	 * The change listener on textField
	 */
	private ChangeListener<Number> heightL;
	/**
	 * The object itself
	 */
	private Rope rope;

	/**
	 * Sets the observable of the header. The parameters of the textfield will
	 * be charged to the object's important parameters.
	 */
	@Override
	public void setObservable(AbstractComplexObservable object) {
		if (object instanceof Rope) {
			this.rope = (Rope) object;

			this.height.setValue(rope.getRopeHeight());

			/*
			 * Removes old listeners
			 */
			if (heightL != null) {
				this.height.valueProperty().removeListener(heightL);

			}

			/*
			 * Creates the listeners for new value
			 */
			this.heightL = ((value, old, newv) -> {
				rope.setRopeHeight(newv.doubleValue());
			});

			/*
			 * add the new listeners
			 */
			this.height.valueProperty().addListener(heightL);

		} else {
			throw new IllegalArgumentException("Cannot operate with " + object.getClass());
		}

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unBind() {
		if (heightL != null) {
			this.height.valueProperty().removeListener(heightL);

		}
	}
	/**
	 * Initializes the numberfield's restriction matching the Settings
	 * parameter.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.height.setMax(Settings.MAX_ROPE_LENGTH);
		this.height.setMin(Settings.MIN_ROPE_LENGTH);

	}

}
