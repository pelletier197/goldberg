package views.screenController;

public interface ControlledScreen {

	/**
	 * Allows to a controlled screen to set it's screen controller in memory. A
	 * screen controller will allow to display the desired screen depending on
	 * an event.
	 * 
	 * @param SC
	 *            The screen controller
	 */
	public void setScreenController(ScreenController SC);

	/**
	 * Automatically called when the controlledScreen is set as the displayed
	 * screen in the view. The screen can be reseted and refreshed when this
	 * method is called.
	 */
	public void displayedToScreen();

	/**
	 * Called by the screenController when a screen is removed from the actual
	 * screen. The screen can then stop any useless thread heavy for the memory
	 */
	public void removedFromScreen();
}
