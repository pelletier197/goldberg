package views.screenController;

public interface LoadingController {

	/**
	 * Starts the loader to load. Stops animation and background threads.
	 */
	public void startLoading();

	/**
	 * Stops the loader to load. Stops animation and background Threads.
	 */
	public void stopLoading();
}
