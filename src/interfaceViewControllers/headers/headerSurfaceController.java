package interfaceViewControllers.headers;

import java.net.URL;
import java.util.ResourceBundle;

import game.Settings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.NumberField;
import observables.AbstractComplexObservable;
import observables.Surface;

public class headerSurfaceController implements Header, Initializable {
	@FXML
	private NumberField width;
	@FXML
	private NumberField height;

	/**
	 * The object itself
	 */
	private Surface surface;

	/**
	 * The change listeners on textFields
	 */
	private ChangeListener<Number> widthL, heightL;

	/**
	 * Sets the observable of the header. The parameters of the textfield will
	 * be charged to the object's important parameters.
	 */
	@Override
	public void setObservable(AbstractComplexObservable object) {
		if (object instanceof Surface) {
			this.surface = (Surface) object;
			this.width.setValue(surface.getWidth());
			this.height.setValue(surface.getHeight());

			/*
			 * Removes old listeners
			 */
			if (widthL != null) {
				this.width.valueProperty().removeListener(widthL);
				this.height.valueProperty().removeListener(heightL);
			}

			/*
			 * Creates the listeners for new value
			 */
			this.widthL = ((value, old, newv) -> {
				surface.setWidth(newv.doubleValue());
			});
			this.heightL = ((value, old, newv) -> {
				surface.setHeight(newv.doubleValue());
			});

			/*
			 * add the new listeners
			 */
			this.width.valueProperty().addListener(widthL);
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
		if (widthL != null) {
			this.width.valueProperty().removeListener(widthL);
			this.height.valueProperty().removeListener(heightL);
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
	}
}
