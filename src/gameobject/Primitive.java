package gameobject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import physics.Vector;

/**
 * 
 * Represents a gameobject prototype which has not been created yet.
 * You can create multiple gameobjects from this by using create()
 * 
 * @author jafi2
 *
 */
public class Primitive {

	/**
	 * Components the created gameobject will have
	 */
	private ArrayList<_Component> components;
	/**
	 * the layer the created Gameobject will be updated and rendered on
	 */
	private int layer;
	
	/**
	 * Create a new primitive Gameobject
	 * @param layer the layer the created Gameobject will be updated and rendered on
	 */
	public Primitive(int layer) {
		components = new ArrayList<>();
		this.layer = layer;
	}
	
	/**
	 * Create a new Gameobject form the primitive. Call init to start drawing and updating the gameobject
	 * @return the created gameobject
	 */
	public Gameobject create() {
		ArrayList<Component> c = new ArrayList<>(components.size());
		for(int i = 0; i < components.size(); i++) {
			c.add(components.get(i).create());
		}
		return new Gameobject(c, layer);
	}
	
	/**
	 * Create a new Gameobject form the primitive. Call init to start drawing and updating the gameobject
	 * @param pos the initial position of the created gameobject
	 * @param size the initial size of the created gameobject
	 * @param velocity the initial velocity of the created gameobject
	 * @return the created gameobject
	 */
	public Gameobject create(Vector pos, Vector size, Vector velocity) {
		ArrayList<Component> c = new ArrayList<>(components.size());
		for(int i = 0; i < components.size(); i++) {
			c.add(components.get(i).create());
		}
		Gameobject g = new Gameobject(c, layer);
		g.pos.x = pos.x;
		g.pos.y = pos.y;
		g.size.x = size.x;
		g.size.y = size.y;
		g.velocity.x = velocity.x;
		g.velocity.y = velocity.y;
		return g;
	}
	
	/**
	 * Create a new Gameobject form the primitive. Call init to start drawing and updating the gameobject
	 * @param pos the initial position of the created gameobject
	 * @param size the initial size of the created gameobject
	 * @return the created gameobject
	 */
	public Gameobject create(Vector pos, Vector size) {
		ArrayList<Component> c = new ArrayList<>(components.size());
		for(int i = 0; i < components.size(); i++) {
			c.add(components.get(i).create());
		}
		Gameobject g = new Gameobject(c, layer);
		g.pos.x = pos.x;
		g.pos.y = pos.y;
		g.size.x = size.x;
		g.size.y = size.y;
		return g;
	}
	
	/**
	 * Create a new Gameobject form the primitive. Call init to start drawing and updating the gameobject
	 * @param pos the initial position of the created gameobject
	 * @return the created gameobject
	 */
	public Gameobject create(Vector pos) {
		ArrayList<Component> c = new ArrayList<>(components.size());
		for(int i = 0; i < components.size(); i++) {
			c.add(components.get(i).create());
		}
		Gameobject g = new Gameobject(c, layer);
		g.pos.x = pos.x;
		g.pos.y = pos.y;
		return g;
	}
	
	/**
	 * Add a component to the primitive
	 * @param component the component to add
	 */
	public void addComponent(Class<? extends Component> component) {
		
		if(hasComponent(component)) {
			throw new RuntimeException("Gameobject already contains this component");
		}
		
		components.add(new _Component(component, null));
		
	}
	
	/**
	 * Add a component to the primitive with the parameters the constructor of the component should use while creating the gameobject
	 * @param component the component to add
	 * @param parameters parameters for the components constructor. Wrong parameters may throw an exception during creation
	 */
	public void addComponent(Class<? extends Component> component, Object... parameters) {
		
		if(hasComponent(component)) {
			throw new RuntimeException("Gameobject already contains this component");
		}
		
		components.add(new _Component(component, parameters));
		
	}
	
	/**
	 * Does the primitive contain this component
	 * @param check the class of the component of a subclass of the component which should be check for
	 * @return does the primitive contain the component
	 */
	public boolean hasComponent(Class<? extends Component> check) {
		
		for(int i = 0; i < components.size(); i++) {
			if(check.isAssignableFrom(components.get(i).component)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * 
	 * Container class for saving components and constructor parameters and creating the component
	 * 
	 * @author jafi2
	 *
	 */
	private static final class _Component {
		
		/**
		 * The component
		 */
		public Class<? extends Component> component;
		/**
		 * The constructor parameters
		 */
		public Object[] parameters;
		
		/**
		 * Create a new Container object
		 * @param component the component
		 * @param parameters the constructor parameters
		 */
		public _Component(Class<? extends Component> component, Object[] parameters) {
			if(component == null) throw new RuntimeException("Component must not be null");
			this.component = component;
			this.parameters = parameters;
		}
		
		/**
		 * Create the Component contained by this instance
		 * @return the created component
		 */
		public Component create() {
			if(parameters == null) {
				try {
					return component.getConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new RuntimeException("Could not create Gameobject");
				}
			} else {
				try {
					Class<?>[] pTypes = new Class<?>[parameters.length];
					for(int i = 0; i < parameters.length; i++) {
						pTypes[i] = parameters[i].getClass();
					}
					return component.getConstructor(pTypes).newInstance(parameters);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new RuntimeException("Could not create Gameobject");
				}
			}
		}
		
	}
	
}
