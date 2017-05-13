package player;

import java.io.Serializable;

/**
 * 
 * This is the interface for all weapons
 * 
 * @author jafi2
 *
 */
public interface Weapon extends Serializable {

	/*
	 * TODO improve this (provide basic functionality) 
	 */
	
	/**
	 * Update the weapon
	 * @param deltaTime time since last update
	 */
	public void update(double deltaTime);
	/**
	 * Render the weapon
	 */
	public void render();
	
}
