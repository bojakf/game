package levels;

/**
 * 
 * This is a superclass for Game logic to switch to another level at any point in the game<br>
 * e.g. Main Menu, a Game, ...
 * 
 * @author jafi2
 *
 */
public abstract class Level {

	/**
	 * 
	 * This method is called once a frame
	 * All calculations should be done in here
	 * 
	 * @param deltaTime time since last update
	 */
	public abstract void update(double deltaTime);
	
	/**
	 * This method is called once a frame after update
	 * All rendering must be done in here
	 */
	public abstract void render();
	
	/**
	 * Called when the level is no longer current in Game
	 */
	public abstract void onClose();
	
}
