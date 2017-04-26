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
	
	private boolean pendingDestroy = false;
	
	protected Vector pos;
	protected Vector size;
	protected Vector velocity;
	protected int layer;
	
	public boolean sendCollision = true;
	public boolean reveiveCollision = true;
	public boolean isBlocking = false;
	public boolean isStatic = false;
	
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
	
	protected final void physicsUpdate(double deltaTime) {
		pos.x += velocity.x * deltaTime;
		pos.y += velocity.y * deltaTime;
	}
	
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
	
	public final void destroy() {
		pendingDestroy = true;
	}
	
	public final boolean isPendingDestroy() {
		return pendingDestroy;
	}
	
	public final Vector getPos() {
		return pos;
	}

	public final Vector getSize() {
		return size;
	}

	public final Vector getVelocity() {
		return velocity;
	}

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
	protected abstract void onDestroy();
	@Deprecated
	public abstract Object[] getHitInfo();
	
}
