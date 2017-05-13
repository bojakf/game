package physics;

import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;

import gameobject.Component;
import main.Game;

/**
 * 
 * Collider component for physical calculation
 * 
 * @author jafi2
 *
 */
public class Collider extends Component {

	/*
	 * TODO move this to components package
	 */
	/*
	 * TODO final colliders
	 */
	/*
	 * TODO add bounciness to collider
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3988997315667362511L;
	
	/**
	 * position of the collider(middle)
	 * TODO make this final
	 */
	protected Vector pos;
	/**
	 * size of the collider
	 * TODO make this final
	 */
	protected Vector size;
	/**
	 * The velocity of the collider
	 * TODO make this final
	 */
	protected Vector velocity;
	/**
	 * The physical layer of the collider
	 */
	protected int layer = Physics.LAYER_DEFAULT;
	
	/**
	 * Can the collider cause collision events
	 */
	public boolean sendCollision = true;
	/**
	 * Can the collider receive collision events
	 */
	public boolean receiveCollision = true;
	/**
	 * Can other objects pass through the collider<br>
	 * if on one of the colliders has isBlocking == false the colliders can pass through each other.<br>
	 * onCollision will still be called
	 */
	public boolean isBlocking = false;
	/**
	 * can the collider be moved
	 * TODO replace this with endless mass after implementing force into physics
	 */
	public boolean isStatic = false;
	
	/**
	 * Create a new collider
	 * @param layer the physical layer of the collider
	 */
	public Collider(Integer layer) {
		this.layer = layer;
		if(layer == Physics.LAYER_WORLD) {
			isBlocking = true;
			isStatic = true;
		}
	}
	
	/**
	 * Create a new Collider
	 * @param layer the physical layer of the collider
	 * @param isBlocking can other colliders pass through this one?
	 * @param isStatic can the collider be moved?
	 */
	public Collider(Integer layer, Boolean isBlocking, Boolean isStatic) {
		this(layer);
		this.isBlocking = isBlocking;
		this.isStatic = isStatic;
	}
	
	/**
	 * Create a new Collider
	 * @param layer the physical layer of the collider
	 * @param isBlocking can other colliders pass through this one?
	 * @param isStatic can the collider be moved?
	 * @param sendCollision can the collider send a collision event to others?
	 * @param receiveCollision can the collider receive collision event from others?
	 */
	public Collider(Integer layer, Boolean isBlocking, Boolean isStatic, Boolean sendCollision, Boolean receiveCollision) {
		this(layer, isBlocking, isStatic);
		this.sendCollision = sendCollision;
		this.receiveCollision = receiveCollision;
	}
	
	
	@Override
	public void start() {
		
		pos = parent.pos;
		size = parent.size;
		velocity = parent.velocity;
		
		if((layer & ~Physics.RAYCAST_IGNORE) < 0 || (layer & ~Physics.RAYCAST_IGNORE) >= Physics.LAYERS) {
			new Exception("Invalid layer! Using DEFAULT").printStackTrace();
			layer = Physics.LAYER_DEFAULT;
		}
		
		Physics.registerCollider(this);
		
	}

	@Override
	public void update(double deltaTime) {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void onDestroy() {
		
	}
	
	/**
	 * draw the collider wireframe
	 */
	public final void draw() {
		
		glLineWidth(4f);
		glDisable(GL_TEXTURE_2D);
		
		double x = Game.QUAD_SIZE*parent.size.x*0.5d;
		double y = Game.QUAD_SIZE*parent.size.y*0.5d;
		
		if(isBlocking)
			glColor3d(1, 0, 0);
		else
			glColor3d(0, 0, 1);
		
		glTranslated(parent.pos.x*Game.QUAD_SIZE, parent.pos.y*Game.QUAD_SIZE, 0);
		
		glBegin(GL_LINES);
		
		glVertex2d(-x, -y);
		glVertex2d(x, -y);
		
		glVertex2d(-x, -y);
		glVertex2d(-x, y);
		
		glVertex2d(-x, y);
		glVertex2d(x, y);
		
		glVertex2d(x, -y);
		glVertex2d(x, y);
		
		glEnd();
		
		glTranslated(-(parent.pos.x*Game.QUAD_SIZE), -(parent.pos.y*Game.QUAD_SIZE), 0);
		glEnable(GL_TEXTURE_2D);
		
	}
	
	/**
	 * get the current physical layer of the object
	 * @return physical layer
	 */
	public final int getLayer() {
		return layer;
	}
	
	/**
	 * Set the physical layer of the object
	 * @param layer the physical layer of the object
	 */
	public final void setLayer(int layer) {
		this.layer = layer;
	}
	
	/**
	 * called by physics update in Physics class
	 * 
	 * @param hit object hit
	 */
	public void onCollision(Collider hit) {
		
		ArrayList<Component> components = parent.getComponents();
		
		for(int i = 0; i < components.size(); i++) {
			if(!Collider.class.isAssignableFrom(components.get(i).getClass())) {
				components.get(i).onCollision(hit);
			}
		}
		
	}

}
