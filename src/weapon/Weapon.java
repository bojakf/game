package weapon;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * This is the abstract class for all weapons
 * 
 * @author jafi2
 *
 */
public abstract class Weapon implements Serializable {

	/*
	 * TODO improve this (provide basic functionality) 
	 */
	
	public String texName = null;
	
	/**
	 * Update the weapon
	 * @param deltaTime time since last update
	 */
	public abstract void update(double deltaTime);
	/**
	 * Render the weapon
	 */
	public abstract void render();
	
	public abstract void sendNetUpdate(ArrayList<Serializable> data);
	public abstract void receiveNetUpdate(ArrayList<Serializable> data);
	
}
