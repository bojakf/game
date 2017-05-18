package gameobject;

import java.io.Serializable;

import physics.Collider;

/**
 * 
 * This is the superclass for all components of a gameobject (e.g Collider)
 * 
 * @author jafi2
 *
 */
public abstract class Component implements Serializable {

	/*
	 * TODO set parent in constructor
	 */
	/*
	 * TODO prevent adding same component to multiple gameobjects
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8483226362985916744L;
	
	/**
	 * This is a reference to the gameobject using the component
	 */
	protected Gameobject parent;
	/**
	 * has the start method already been called
	 */
	private transient boolean intialized = false;
	/**
	 * has the component been removed from it's parent or has the parent been destroyed
	 */
	private boolean destroyed = false;
	
	/**
	 * Does nothing at the moment. Parent is not set during constructor call
	 */
	public Component() {}
	
	/**
	 * Called before the first update of the component
	 */
	public abstract void start();
	/**
	 * Called once per frame by the parent. Update the component in here
	 * @param deltaTime time since the last update
	 */
	public abstract void update(double deltaTime);
	/**
	 * Called once per frame after update by the parent. Render the component in here
	 */
	public abstract void render();
	/**
	 * Called when the component is destroyed. Remove input listeners in here
	 */
	protected abstract void onDestroy();
	
	/**
	 * Called if the parent has the collider component and the collider collides with something
	 * @param hit the collider hit
	 */
	public void onCollision(Collider hit) {
		
	}
	
	/**
	 * Set the parent of the component. This method should only be called once.
	 * @param g the parent of the component
	 */
	public void setParent(Gameobject g) {
		parent = g;
	}
	
	/**
	 * get the parent of the component. May be null before parent has been initialized
	 * @return the parent of the component
	 */
	public Gameobject getParent() {
		return parent;
	}

	/**
	 * Has the component been initialized
	 * @return Has the component been initialized
	 */
	public boolean isIntialized() {
		return intialized;
	}
	
	/**
	 * Called before the first update of the component. This method must only be called by gameobject
	 */
	public void initialize() {
		intialized = true;
		start();
	}
	
	/**
	 * Has the component been destroyed
	 * @return Has the component been destroyed
	 */
	public boolean isDestroyed() {
		return destroyed;
	}
	
	/**
	 * Destroy the component. Automatically called if gameobject is destroyed, component is removed from the parent or 
	 * the garbage collector removes the component.
	 */
	public void destroy() {
		if(destroyed) return;
		destroyed = true;
		onDestroy();
	}
	
	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}
	
}
