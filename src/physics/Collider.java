package physics;

import static org.lwjgl.opengl.GL11.*;

import java.io.Serializable;

import main.Game;

public abstract class Collider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -677833801751417709L;

	/*
	 * TODO final colliders
	 */
	/*
	 * TODO add bounciness to collider
	 */
	
	/**
	 * Did the physics thread remove the collider
	 */
	private boolean pendingDestroy = false;
	
	/**
	 * position of the collider
	 */
	protected Vector pos;
	/**
	 * size of the collider
	 */
	protected Vector size;
	protected Vector velocity;
	protected int layer;
	
	public boolean sendCollision = true;
	public boolean reveiveCollision = true;
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
	 * Create the collider, and add it to physics
	 * @param pos the initial position of the Collider
	 * @param size the initial size of the Collider
	 * @param velocity the initial velocity of the Collider
	 * @param layer the initial layer of the Collider
	 */
	public Collider(Vector pos, Vector size, Vector velocity, int layer) {
		
		if(size.x < 0
				|| size.y < 0) {
			new Exception("Negative size of collider will produce problems").printStackTrace();
		}
		
		/*
		 * Set Default for world isStatic
		 * Set Default for world isBlocking
		 */
		if(layer == Physics.LAYER_WORLD) {
			isBlocking = true;
			isStatic = true;
		}
		
		this.pos = pos;
		this.size = size;
		this.velocity = velocity;
		
		this.layer = layer;
		if(layer < 0 || layer >= Physics.LAYERS) {
			new Exception("Invalid layer! Using DEFAULT").printStackTrace();
			layer = Physics.LAYER_DEFAULT;
		}
		
		Physics.registerCollider(this);
		
	}
	
	/**
	 * called once every pyhsicsUpdate
	 * @param deltaTime time since the last update
	 */
	protected final void physicsUpdate(double deltaTime) {
		pos.x += velocity.x * deltaTime;
		pos.y += velocity.y * deltaTime;
	}
	
	/**
	 * draw the collider wireframe
	 */
	public final void draw() {
		
		glLineWidth(4f);
		glColor3d(1, 0, 0);
		
		glBegin(GL_LINES);
		
		glVertex2d(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE);
		glVertex2d(pos.x * Game.QUAD_SIZE + size.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE);
		
		glVertex2d(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE);
		glVertex2d(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE);
		
		glVertex2d(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE);
		glVertex2d(pos.x * Game.QUAD_SIZE + size.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE);
		
		glVertex2d(pos.x * Game.QUAD_SIZE + size.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE);
		glVertex2d(pos.x * Game.QUAD_SIZE + size.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE);
		
		glEnd();
		
	}
	
	/**
	 * destroy the collider
	 */
	public final void destroy() {
		pendingDestroy = true;
	}
	
	/**
	 * is the collider queued for deletion 
	 * @return pendingDestroy
	 */
	public final boolean isPendingDestroy() {
		return pendingDestroy;
	}
	
	/**
	 * get the current physical layer of the object
	 * @return physical layer
	 */
	public final int getLayer() {
		return layer;
	}
	
	/**
	 * called by physics update in Physics class
	 * 
	 * call hit.getHitInfo() for additional object information
	 * 
	 * @param hit object hit
	 */
	public abstract void onCollision(Collider hit);
	
	/**
	 * Called by physics thread when the collider gets removed
	 */
	protected abstract void onDestroy();
	
}
