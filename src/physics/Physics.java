package physics;

import static org.lwjgl.opengl.GL11.glLineWidth;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

/**
 *
 * Contains physical calculations
 * 
 * @author jafi2
 *
 */
public class Physics {
	
	/**
	 * Number of available layers
	 */
	public static final int LAYERS = 5;
	
	/**
	 * default physical layer <br>will sometimes automatically replace invalid layers
	 * can collide with all other layer
	 */
	public static final int LAYER_DEFAULT = 0;
	/**
	 * physical layer which should only be used for players
	 */
	public static final int LAYER_PLAYER = 1;
	/**
	 * physical layer for enemies
	 */
	public static final int LAYER_ENEMY = 2;
	/**
	 * physical layer for world
	 */
	public static final int LAYER_WORLD = 3;
	
	/**
	 * This value is intended for raycasting in order to ignore the isBlocking value
	 * it can be used with bitwise or on the layer
	 */
	public static final int RAYCAST_ALL = 0x80000000;
	/**
	 * This value is intended for raycasting in order ignore the object which has this bit
	 * set. It does not affect other collisions. Use it bitwise or on the layer of the collider
	 */
	public static final int RAYCAST_IGNORE = 0x40000000;
	
	
	/**
	 * Placeholder for collisionMatrix
	 */
	private static boolean NONE = false;
	/**
	 * holds information with which layers another can collide
	 */
	private static boolean collisionMatrix[][] = new boolean[][] {
					//Default	Player	Enemy	World
		/*Default*/	{true,		true,	true,	true},
		/*Player*/	{NONE,		false,	false,	true},
		/*Enemy*/	{NONE,		NONE,	false,	true},
		/*World*/	{NONE,		NONE,	NONE,	false},
	};
	
	/**
	 * contains all active colliders
	 */
	private static ArrayList<Collider> colliders = new ArrayList<Collider>();
	
	/**
	 * This class contains only static methods
	 */
	private Physics() {}
	
	/**
	 * Adds collider to physics
	 * @param col Collider to add
	 */
	public static void registerCollider(Collider col) {
		colliders.add(col);
	}
	
	/*
	 * FIXME use force instead of velocity
	 * TODO add drag
	 * TODO moving objects collision
	 */
	
	/**
	 * This method should be called regularly if the game is not paused
	 * <ul>
	 * <li>Updates the positions of all physicals objects</li>
	 * <li>calls collision Events</li>
	 * </ul>
	 * @param deltaTime time since last calculation
	 */
	public static void physicsUpdate(double deltaTime) {
		
		if(deltaTime>1d/20d) System.err.println("Low framerates may cause problems. Cur: " + 1/deltaTime);
		
		/*
		 * 
		 * Movement calculation
		 * and TmpCollider generation
		 * 
		 */
		
		for(int a = 0; a < colliders.size(); a++) {
			
			Collider c = colliders.get(a);
			if(c == null || c.isDestroyed()) {
				colliders.remove(a);
				a--;
				continue;
			}
			
			if(c.isStatic) continue;
			c.pos.x += c.velocity.x * deltaTime;
			c.pos.y += c.velocity.y * deltaTime;
			
		}
			
		for(int a = 0; a < colliders.size(); a++) {
			
			Collider c1 = colliders.get(a);
			if(c1 == null) {
				colliders.remove(a);
				a--;
				continue;
			}
			
			if(!c1.receiveCollision
					&& !c1.sendCollision)
				continue;
			
			for(int b = a+1; b < colliders.size(); b++) {
				
				Collider c2 = colliders.get(b);
				if(c2 == null) {
					colliders.remove(b);
					b--;
					continue;
				}
				
				if(c1.isStatic && c2.isStatic) continue;
				
				if((!c2.receiveCollision && !c2.sendCollision)
						|| (!c2.receiveCollision && !c1.receiveCollision)
						|| (!c2.sendCollision && !c1.sendCollision))
					continue;
				
				if(!_canCollide(c1.layer, c2.layer)) continue;
				
				Vector pos1 = new Vector(c1.pos.x - c1.size.x*0.5d, c1.pos.y - c1.size.y*0.5);
				Vector pos2 = new Vector(c2.pos.x - c2.size.x*0.5d, c2.pos.y - c2.size.y*0.5);
				
				if(pos1.x <= pos2.x + c2.size.x && pos1.x + c1.size.x >= pos2.x &&
						pos1.y <= pos2.y + c2.size.y && pos1.y + c1.size.y >= pos2.y) {
					
					if(c1.receiveCollision && c2.sendCollision) c1.onCollision(colliders.get(b));
					if(c2.receiveCollision && c1.sendCollision) c2.onCollision(colliders.get(a));
					
					
					
					/*
					 * Did one of the colliders get destroyed?
					 */
					if(c1.isDestroyed()) {
						colliders.remove(a).onDestroy();
						a--;
						b--;
						break;
					}
					if(c2.isDestroyed()) {
						colliders.remove(b).onDestroy();
						b--;
						continue;
					}
					
					
					/*
					 * Move colliders outside of each other
					 */
					if(c1.isBlocking && c2.isBlocking) {
						
						if(c1.isStatic) {
							Collider tmp = c1;
							c1 = c2;
							c2 = tmp;
						} else if(!c2.isStatic) {
							if(c2.velocity.length() > c1.velocity.length()) {
								Collider tmp = c1;
								c1 = c2;
								c2 = tmp;
							}
						}
						
						pos1 = c1.pos;
						pos2 = c2.pos;
						
						double mx = pos1.x - pos2.x;
						double my = pos1.y - pos2.y;
						
						if(mx > 0) {
							mx = c2.size.x*0.5+c1.size.x*0.5 - mx;
						} else {
							mx = -(mx + c1.size.x*0.5+c2.size.x*0.5);
						}
						if(my > 0) {
							my = c2.size.y*0.5+c1.size.y*0.5 - my;
						} else {
							my = -(my + c1.size.y*0.5+c2.size.y*0.5);
						}
						
						if(Math.abs(mx) < Math.abs(my)) {
							pos1.x += mx;
						} else {
							pos1.y += my;
						}
					
					}
					
				}				
				
			}
			
		}
		
	}
	
	/**
	 * Draw collider wireframe for debugging
	 */
	public static void drawColliders() {
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		glLineWidth(4f);
		GL11.glTranslated(0, 0, -10);
		
		for(int i = 0; i < colliders.size(); i++) {
			colliders.get(i).draw();
		}
		
		GL11.glTranslated(0, 0, 10);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	/**
	 * 
	 * Raycast method using default layer
	 * 
	 * @param ray Ray to cast
	 * @return hit infomation @see {@link RaycastHit}
	 */
	public static RaycastHit raycast(Ray ray) {
		return raycast(ray, LAYER_DEFAULT);		
	}
	
	/**
	 * 
	 * Raycast method<br>
	 * <b>Info: </b> layer | RAYCAST_ALL in order to ignore the isBlocking value in collider
	 * 
	 * @param ray 			Ray to cast
	 * @param layer			Layer which should be used for raycast
	 * @return				hit infomation @see {@link RaycastHit}
	 */
	public static RaycastHit raycast(Ray ray, int layer) {
		
		boolean ignoreBlocking = false;
		if((layer | RAYCAST_ALL) != 0) {
			layer = layer & ~RAYCAST_ALL;
			ignoreBlocking = true;
		}
		
		if(layer < 0 || layer > LAYERS) {
			new Exception("Invalid Layer! using default").printStackTrace();
			layer = LAYER_DEFAULT;
		}
		
		ArrayList<RaycastHit> hits = new ArrayList<RaycastHit>();
		
		for(int i = 0; i < colliders.size(); i++) {
			
			Collider col = colliders.get(i);
			if(!ignoreBlocking && !col.isBlocking) continue;
			
			if(!_canCollide(col.layer, layer)) continue;
			
			Vector min = new Vector(col.pos.x-col.size.x*0.5, col.pos.y-col.size.y*0.5);
			Vector max = new Vector(col.pos.x + col.size.x*0.5, col.pos.y + col.size.y*0.5);
			Vector dis = new Vector(-1,-1);
			
			boolean inside = true;
			
			if(ray.origin.x < min.x) {
				inside = false;
				dis.x = (min.x-ray.origin.x)/ray.dir.x;
			} else if(ray.origin.x > max.x) {
				inside = false;
				dis.x = (max.x-ray.origin.x)/ray.dir.x;
			}
			
			if(ray.origin.y < min.y) {
				inside = false;
				dis.y = (min.y-ray.origin.y)/ray.dir.y;
			} else if(ray.origin.y > max.y) {
				inside = false;
				dis.y = (max.y-ray.origin.y)/ray.dir.y;
			}
			
			/*
			 * Ray starts inside of collider 
			 */
			if(inside) {
				RaycastHit hit = new RaycastHit();
				hit.distance = 0;
				hit.hit = col;
				hit.pos = ray.origin;
				hits.add(hit);
				continue;
			}
			
			double plane = dis.x;
			if(dis.y < plane) {
				plane = dis.y;
			}
			
			RaycastHit hit = new RaycastHit();
			hit.hit = col;
			hit.pos = new Vector();
			
			hit.pos.x = ray.origin.x + plane * ray.dir.x;
			if(hit.pos.x < min.x || hit.pos.x > max.x) continue;
			
			hit.pos.y = ray.origin.y + plane * ray.dir.y;
			if(hit.pos.y < min.y || hit.pos.y > max.y) continue;
			
			
			hit.distance = Vector.substract(ray.origin, hit.pos).length();
			hits.add(hit);
			
		}
		
		if(hits.size() == 0) return null;
		
		RaycastHit hit = hits.get(0);
		for(RaycastHit h : hits) {
			if(h.distance < hit.distance) {
				hit = h;
			}
		}
		
		return hit;
		
	}
	
	/**
	 * 
	 * Raycast method with ability to ignore certain objects<br>
	 * <b>Info: </b> layer | RAYCAST_ALL in order to ignore the isBlocking value in collider
	 * 
	 * @param ray 			Ray to cast
	 * @param layer			Layer which should be used for raycast
	 * @param ignore		Colliders to ignore, this value must not be null
	 * @return				hit information @see {@link RaycastHit}
	 */
	public static RaycastHit raycast(Ray ray, int layer, ArrayList<Collider> ignore) {
		
		boolean ignoreBlocking = false;
		if((layer | RAYCAST_ALL) != 0) {
			layer = layer & ~RAYCAST_ALL;
			ignoreBlocking = true;
		}
		
		if(layer < 0 || layer > LAYERS) {
			new Exception("Invalid Layer! using default").printStackTrace();
			layer = LAYER_DEFAULT;
		}
		
		ArrayList<RaycastHit> hits = new ArrayList<RaycastHit>();
		
		for(int i = 0; i < colliders.size(); i++) {
			
			Collider col = colliders.get(i);
			if(!ignoreBlocking && !col.isBlocking) continue;
			
			if(!_canCollide(col.layer, layer)) continue;
			
			if((col.layer & RAYCAST_IGNORE) != 0) continue;
			
			if(ignore.contains(col)) continue;
			
			Vector min = new Vector(col.pos.x-col.size.x*0.5, col.pos.y-col.size.y*0.5);
			Vector max = new Vector(col.pos.x + col.size.x*0.5, col.pos.y + col.size.y*0.5);
			Vector dis = new Vector(-1,-1);
			
			boolean inside = true;
			
			if(ray.origin.x < min.x) {
				inside = false;
				dis.x = (min.x-ray.origin.x)/ray.dir.x;
			} else if(ray.origin.x > max.x) {
				inside = false;
				dis.x = (max.x-ray.origin.x)/ray.dir.x;
			}
			
			if(ray.origin.y < min.y) {
				inside = false;
				dis.y = (min.y-ray.origin.y)/ray.dir.y;
			} else if(ray.origin.y > max.y) {
				inside = false;
				dis.y = (max.y-ray.origin.y)/ray.dir.y;
			}
			
			/*
			 * Ray starts inside of collider 
			 */
			if(inside) {
				RaycastHit hit = new RaycastHit();
				hit.distance = 0;
				hit.hit = col;
				hit.pos = ray.origin;
				hits.add(hit);
				continue;
			}
			
			double plane = dis.x;
			if(dis.y < plane) {
				plane = dis.y;
			}
			
			RaycastHit hit = new RaycastHit();
			hit.hit = col;
			hit.pos = new Vector();
			
			hit.pos.x = ray.origin.x + plane * ray.dir.x;
			if(hit.pos.x < min.x || hit.pos.x > max.x) continue;
			
			hit.pos.y = ray.origin.y + plane * ray.dir.y;
			if(hit.pos.y < min.y || hit.pos.y > max.y) continue;
			
			
			hit.distance = Vector.substract(ray.origin, hit.pos).length();
			hits.add(hit);
			
		}
		
		if(hits.size() == 0) return null;
		
		RaycastHit hit = hits.get(0);
		for(RaycastHit h : hits) {
			if(h.distance < hit.distance) {
				hit = h;
			}
		}
		
		return hit;
		
	}
	
	/**
	 * Get all colliders interfering the circle
	 * @param pos the position of the circle
	 * @param radius the radius of the circle
	 * @return list of colliders interfering with the circle
	 */
	public static ArrayList<Collider> checkCircle(Vector pos, double radius) {
		
		ArrayList<Collider> r = new ArrayList<>();
		
		for(int i = 0; i < colliders.size(); i++) {
			
			Collider c = colliders.get(i);
			
			Vector p1 = new Vector(c.pos.x-c.size.x*0.5 - pos.x, c.pos.y-c.size.y*0.5 - pos.y);
			Vector p2 = new Vector(c.pos.x+c.size.x*0.5 - pos.x, c.pos.y+c.size.y*0.5 - pos.y);
			Vector p3 = new Vector(p2.x, p1.y);
			Vector p4 = new Vector(p1.x, p2.y);
			
			if(p1.length() <= radius) {
				r.add(c);
				continue;
			}
			if(p2.length() <= radius) {
				r.add(c);
				continue;
			}
			if(p3.length() <= radius) {
				r.add(c);
				continue;
			}
			if(p4.length() <= radius) {
				r.add(c);
				continue;
			}
			
		}
		
		return r;
		
	}
	
	/**
	 * Check if this layers can collide
	 * @param layer1 first layer
	 * @param layer2 second layer
	 * @return can they collide?
	 */
	public static boolean canCollide(int layer1, int layer2) {
		
		if(layer1 >= LAYERS || layer2 >= LAYERS || layer1 < 0 || layer2 < 0) {
			new Exception("Invalid layer!").printStackTrace();
			return false;
		}
		
		return _canCollide(layer1, layer2);
		
	}
	
	/**
	 * {@link canCollide} for internal use (no error checking)
	 * @param layer1 first layer
	 * @param layer2 second layer
	 * @return can they collide?
	 */
	private static boolean _canCollide(int layer1, int layer2) {
		
		layer1 &= ~RAYCAST_IGNORE;
		layer2 &= ~RAYCAST_IGNORE;
		
		if(layer2 < layer1) {
			int tmp = layer1;
			layer1 = layer2;
			layer2 = tmp;
		}
		
		return collisionMatrix[layer1][layer2];
		
	}
	
}
