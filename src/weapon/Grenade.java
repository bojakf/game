package weapon;

import java.util.ArrayList;

import components.Damageable;
import components.Player;
import gameobject.Component;
import gameobject.Gameobject;
import main.Primitives;
import physics.Collider;
import physics.Physics;
import physics.Vector;


/**
 * 
 * Component for the projectile create by the grenadeLauncher
 * 
 * @author jafi2
 *
 */
public class Grenade extends Component {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6729058073098114985L;

	/**
	 * The size of the grenade
	 */
	public static final Vector SIZE = new Vector(0.5d, 0.5d);
	/**
	 * The speed of the grenade
	 */
	private static final double SPEED = 10;
	/**
	 * The start time until the grenade explodes
	 */
	private static final double TIME_LEFT = 3;
	/**
	 * The size of the explosion
	 */
	private static final double EXPLOSION_RADIUS = 2;
	/**
	 * The damage of the explosion
	 */
	private static final double EXPLOSION_DAMAGE = 50;
	
	/**
	 * The position the grenade should hit
	 */
	private final Vector destination = new Vector();
	
	/**
	 * Used to stop the grenade after it hit the destination
	 */
	private double lastDist = Double.MAX_VALUE;
	/**
	 * Time until the grenade explodes.
	 */
	private double timeLeft = TIME_LEFT;
	
	/**
	 * Does nothing at the moment
	 */
	public Grenade() {
		
	}
	
	@Override
	public void start() {
		
	}
	
	@Override
	public void update(double deltaTime) {
		
		/*
		 * Movement
		 */
		Vector dist = Vector.substract(destination, parent.pos);
		if(dist.length() > lastDist) {
			lastDist = dist.length();
			parent.velocity.x = 0;
			parent.velocity.y = 0;
			parent.pos.set(destination);
		} else {
			lastDist = dist.length();
			dist.normalize();
			dist.scale(SPEED);
			parent.velocity.set(dist);
		}
		
		
		/*
		 * Exploding
		 */
		timeLeft -= deltaTime;
		if(timeLeft <= 0) {
			ArrayList<Collider> hit = Physics.checkCircle(parent.pos, EXPLOSION_RADIUS);
			for(int i = 0; i < hit.size(); i++) {
				if(hit.get(i).getParent().hasComponent(Damageable.class)) {
					((Damageable)hit.get(i).getParent().getComponent(Damageable.class)).damage(EXPLOSION_DAMAGE);
				}
			}
			
			Gameobject explosion = Primitives.explosion.create(
					new Vector(parent.pos.x, parent.pos.y), 
					new Vector(EXPLOSION_RADIUS*2, EXPLOSION_RADIUS*2));
			explosion.init();
			Gameobject crater = Primitives.crater.create(
					new Vector(parent.pos.x, parent.pos.y), 
					new Vector(EXPLOSION_RADIUS*2, EXPLOSION_RADIUS*2));
			crater.init();
			parent.destroy();
		}
		
	}
	
	@Override
	public void render() {
		
	}

	@Override
	public void onCollision(Collider hit) {
		if(hit.isBlocking && !(hit.getParent().hasComponent(Player.class))) {
			parent.removeComponent(Collider.class);
		}
	}

	@Override
	protected void onDestroy() {
		
	}
	
	/**
	 * Sets the destination of the grenade
	 * @param dest the destination
	 */
	public void setDestination(Vector dest) {
		destination.set(dest);
	}

}
