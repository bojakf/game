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

public class Rocket extends Component {

	/*
	 * TODO add smoke
	 */
	/*
	 * FIXME rocket texture and hitbox
	 */
	
	private Gameobject target = null;
	
	private Player player;
	
	public static final Vector SIZE = new Vector(1,1);
	
	/**
	 * The size of the explosion
	 */
	private static final double EXPLOSION_RADIUS = 1.5;
	/**
	 * The damage of the explosion
	 */
	private static final double EXPLOSION_DAMAGE = 30;
	
	private static final double SPEED = 20;
	
	@Override
	public void start() {
		
	}

	@Override
	public void update(double deltaTime) {
		
		if(target != null) {
			setDest(target.pos);
		}
		
	}

	@Override
	public void render() {
		
	}

	@Override
	protected void onDestroy() {
		
	}
	
	@Override
	public void onCollision(Collider hit) {
		
		if(hit.isBlocking && (!hit.getParent().hasComponent(Damageable.class) || hit.getParent().getComponent(Damageable.class) != player)) {
			
			ArrayList<Collider> h = Physics.checkCircle(parent.pos, EXPLOSION_RADIUS);
			for(int i = 0; i < h.size(); i++) {
				if(h.get(i).getParent().hasComponent(Damageable.class)) {
					((Damageable)h.get(i).getParent().getComponent(Damageable.class)).damage(EXPLOSION_DAMAGE);
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
		
		super.onCollision(hit);
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setDest(Vector dest) {
		Vector v = Vector.substract(dest, getParent().pos);
		v.normalize();
		v.scale(SPEED);
		parent.velocity.set(v);
	}
	
	public void setTarget(Gameobject g) {
		target = g;
		setDest(g.pos);
	}

}
