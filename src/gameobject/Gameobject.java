package gameobject;

import java.io.Serializable;
import java.util.ArrayList;

import main.Game;
import physics.Vector;

/**
 * 
 * Instances of this class represent objects in the games world<br>
 * init() may be called before using the instance
 * 
 * @author jafi2
 *
 */
public class Gameobject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 981694171220677614L;
	
	/**
	 * The layer to draw and update the gameobject on
	 */
	public final int layer;
	
	/**
	 * Has the gameobject been initialized
	 */
	private transient boolean inited = false;
	
	/**
	 * Contains all components assigned to the gameobject
	 */
	private ArrayList<Component> components;
	/**
	 * Has the gameobject been destroyed
	 */
	private boolean destroyed = false;
	
	/**
	 * The position of the gameobject
	 */
	public final Vector pos = new Vector();
	/**
	 * The size of the gameobject
	 */
	public final Vector size = new Vector(1, 1);
	/**
	 * The speed of the gameobject
	 */
	public final Vector velocity = new Vector();
	
	/**
	 * Create a new gameobject with no components assigned to it
	 * @param layer the layer to draw and update the gameobject on
	 */
	public Gameobject(int layer) {
		components = new ArrayList<>();
		this.layer = layer;
	}
	
	/**
	 * Create a new gameobject with a set of components
	 * @param components the components for the gameobject
	 * @param layer the layer to draw and update the gameobject on 
	 */
	public Gameobject(ArrayList<Component> components, int layer) {
		this.components = components;
		if(components == null) {
			components = new ArrayList<>();
		} else {
			for(int i = 0; i < components.size(); i++) {
				components.get(i).setParent(this);
			}
		}
		this.layer = layer;
	}
	
	/**
	 * Initialize the gameobject. May only be called once
	 */
	public void init() {
		if(inited) return;
		Game.addGameobject(this, layer);
	}
	
	/**
	 * Update the gameobject and it's components
	 * @param deltaTime the time since the last update
	 */
	public void update(double deltaTime) {
		if(destroyed) return;
		
		for(int i = 0; i < components.size(); i++) {
			if(components.get(i).isDestroyed()) {
				components.remove(i);
				i--;
				continue;
			}
			if(!components.get(i).isIntialized()) components.get(i).initialize();
			components.get(i).update(deltaTime);
		}
		
	}
	
	/**
	 * Render the gameobject and it's components
	 */
	public void render() {
		if(destroyed) return;
		
		for(int i = 0; i < components.size(); i++) {
			if(!Game.net.isServer() || components.get(i).isIntialized())components.get(i).render();
		}
		
	}
	
	/**
	 * Destroy the gameobject and all it's components
	 */
	public void destroy() {
		if(destroyed) return;
		for(int i = 0; i < components.size(); i++) {
			components.get(i).destroy();
		}
		destroyed = true;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(!destroyed) {
			destroy();
		}
		super.finalize();
	}
	
	/**
	 * Get the components of the gameobject
	 * @return components of the gameobject
	 */
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	/**
	 * Get the specified component. Returns null if there is no such component
	 * @param get the class which must be the same or a subclass of the component which should be returned
	 * @return the component which is assignable form get
	 */
	public Component getComponent(Class<? extends Component> get) {
		for(int i = 0; i < components.size(); i++) {
			if(get.isAssignableFrom(components.get(i).getClass())) return components.get(i);
		}
		return null;
	}
	
	/**
	 * Removes a component from the gameobject
	 * @param remove the class of the component to remove or a subclass of the component
	 * @return true if a component has been removed
	 */
	public boolean removeComponent(Class <? extends Component> remove) {
		
		for(int i = 0; i < components.size(); i++) {
			if(components.get(i).getClass().equals(remove)) {
				components.remove(i).destroy();
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Adds a component to the gameobject. A component must only be added to one gameobject during it's lifetime.
	 * Throws a RuntimeException if there hasComponent(component.getClass()) returns ture
	 * @param component the component to add
	 */
	public void addComponent(Component component) {
		
		if(hasComponent(component.getClass())) {
			throw new RuntimeException("Gameobject already contains this component");
		}
		
		component.setParent(this);
		components.add(component);
		
	}
	
	/**
	 * Does the gameobject contain this component
	 * @param check the class of the component of a subclass of the component which should be check for
	 * @return does the gameobject contain the component
	 */
	public boolean hasComponent(Class<? extends Component> check) {
		
		for(int i = 0; i < components.size(); i++) {
			if(check.isAssignableFrom(components.get(i).getClass())) {
				return true;
			}
		}
		
		return false;
		
	}
	
	
	
}
