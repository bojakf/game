package network;

/**
 * 
 * this is the interface which must be implemented by all objects representing a player
 * 
 * @author jafi2
 *
 */
public interface NetPlayer extends NetObject {
	
	/**
	 * Get the id of the player
	 * @return the id of the player
	 */
	public int getPlayerID();
	
}
