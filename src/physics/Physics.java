package physics;

import static org.lwjgl.opengl.GL11.glLineWidth;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Physics {
	
	public static final int LAYERS = 4;
	
	public static final int LAYER_DEFAULT = 0;
	public static final int LAYER_PLAYER = 1;
	public static final int LAYER_ENEMY = 2;
	public static final int LAYER_WORLD = 3;
	
	private static boolean NONE = false; //Placeholder for collision Matrix
	private static boolean collisionMatrix[][] = new boolean[][] {
					//Default	Player	Enemy	World
		/*Default*/	{true,		true,	true,	true},
		/*Player*/	{NONE,		false,	true,	true},
		/*Enemy*/	{NONE,		NONE,	false,	true},
		/*World*/	{NONE,		NONE,	NONE,	false},
	};
	
	private static ArrayList<Collider> colliders = new ArrayList<Collider>();
	
	public static void registerCollider(Collider col) {
		colliders.add(col);
	}
	
	/*
	 * FIXME use force instead of velocity
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
			
			if(colliders.get(a).isPendingDestroy()) {
				colliders.remove(a).onDestroy();
				a--;
				continue;
			}
			
			if(colliders.get(a).isStatic) continue;
			colliders.get(a).physicsUpdate(deltaTime);
			
		}
			
		for(int a = 0; a < colliders.size(); a++) {
			
			Collider c1 = colliders.get(a);
			
			if(!c1.reveiveCollision
					&& !c1.sendCollision)
				continue;
			
			for(int b = a+1; b < colliders.size(); b++) {
				
				Collider c2 = colliders.get(b);
				
				if(c1.isStatic && c2.isStatic) continue;
				
				if((!c2.reveiveCollision && !c2.sendCollision)
						|| (!c2.reveiveCollision && !c1.reveiveCollision)
						|| (!c2.sendCollision && !c1.sendCollision))
					continue;
				
				if(!_canCollide(c1.layer, c2.layer)) continue;
				
				if(c1.pos.x <= c2.pos.x + c2.size.x && c1.pos.x + c1.size.x >= c2.pos.x &&
						c1.pos.y <= c2.pos.y + c2.size.y && c1.pos.y + c1.size.y >= c2.pos.y) {
					
					if(c1.reveiveCollision && c2.sendCollision) c1.onCollision(colliders.get(b));
					if(c2.reveiveCollision && c1.sendCollision) c2.onCollision(colliders.get(a));
					
					
					
					/*
					 * Did one of the colliders get destroyed?
					 */
					if(c1.isPendingDestroy()) {
						colliders.remove(a).onDestroy();
						a--;
						b--;
						break;
					}
					if(c2.isPendingDestroy()) {
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
							
						double mx = c1.pos.x - c2.pos.x;
						double my = c1.pos.y - c2.pos.y;
						
						if(mx > 0) {
							mx = c2.size.x - mx;
						} else {
							mx = -(mx + c1.size.x);
						}
						if(my > 0) {
							my = c2.size.y - my;
						} else {
							my = -(my + c1.size.y);
						}
						
						if(Math.abs(mx) < Math.abs(my)) {
							c1.pos.x += mx;
						} else {
							c1.pos.y += my;
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
		
		glLineWidth(4f);
		GL11.glTranslated(0, 0, -10);
		
		for(int i = 0; i < colliders.size(); i++) {
			colliders.get(i).draw();
		}
		
		GL11.glTranslated(0, 0, 10);
		
	}
	
	/**
	 * 
	 * Raycast method using default layer
	 * 
	 * @param ray 			Ray to cast
	 * @return				hit infomation
	 */
	public static RaycastHit raycast(Ray ray) {
		return raycast(ray, LAYER_DEFAULT);		
	}
	
	/**
	 * 
	 * Raycast method
	 * 
	 * @param ray 			Ray to cast
	 * @param layer			Layer which should be used for raycast
	 * @return				hit infomation
	 */
	public static RaycastHit raycast(Ray ray, int layer) {
		
		if(layer < 0 || layer > LAYERS) {
			new Exception("Invalid Layer! using default").printStackTrace();
			layer = LAYER_DEFAULT;
		}
		
		ArrayList<RaycastHit> hits = new ArrayList<RaycastHit>();
		
		for(int i = 0; i < colliders.size(); i++) {
			
			Collider col = colliders.get(i);
			if(!col.isBlocking) continue;
			
			if(!_canCollide(col.layer, layer)) continue;
			
			Vector min = new Vector(col.pos.x, col.pos.y);
			Vector max = new Vector(col.pos.x + col.size.x, col.pos.y + col.size.y);
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
	 * Check if colliders at this layers can collide
	 * @param layer1 - first collider
	 * @param layer2 - second collider
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
	 * canCollide for internal use(no error checking)
	 * @param layer1
	 * @param layer2
	 * @return
	 */
	private static boolean _canCollide(int layer1, int layer2) {
		
		if(layer2 < layer1) {
			int tmp = layer1;
			layer1 = layer2;
			layer2 = tmp;
		}
		
		return collisionMatrix[layer1][layer2];
		
	}
	
}