package ui;

/**
 * 
 * Class for contents of ui
 * 
 * @author jafi2
 *
 */
public abstract class UiObject {

	/**
	 * Creates the uiObject
	 * @param ui ui to draw in, must not be null
	 */
	public UiObject(Ui ui) {
		if(ui == null) throw new NullPointerException("ui must not be null");
		ui.registerObject(this);
	}
	
	/**
	 * Update the ui element in here
	 * @param deltaTime the time since the last update
	 */
	protected abstract void update(double deltaTime);
	
	/**
	 * Render the ui in here
	 */
	protected abstract void render();
	
	/**
	 * Called if the ui is destroyed
	 */
	protected abstract void onDestroy();
	
}
