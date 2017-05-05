package player.weapon;

import java.io.Serializable;
import java.util.ArrayList;

import loading.TexManager;

import static org.lwjgl.opengl.GL11.*;

import main.Game;
import physics.Collider;
import physics.Damagable;
import physics.Physics;
import physics.Vector;
import player.Player;
import player.Weapon;
import rendering.effects.AnimatedEffect;
import rendering.effects.TextureEffect;

public class Grenade extends Collider implements Weapon {

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
	private Vector destination;
	
	/**
	 * Used to stop the grenade after it hit the destination
	 */
	private double lastDist = Double.MAX_VALUE;
	/**
	 * Time until the grenade explodes.
	 */
	private double timeLeft = TIME_LEFT;

	/**
	 * Create a new grenade
	 */
	public Grenade(Vector pos, Vector destination) {
		
		super(pos, SIZE, Vector.substract(destination, pos), Physics.LAYER_ENEMY);
		velocity.normalize();
		velocity.scale(SPEED);
		isBlocking = false;
		isStatic = false;
		
		this.destination = destination;
		
		
		Game.net.registerNetObject(this);
		
	}
	
	@Override
	public void update(double deltaTime) {
		
		/*
		 * Movement
		 */
		double dist = Vector.substract(destination, pos).length();
		if(dist > lastDist) {
			velocity.x = 0;
			velocity.y = 0;
			pos = destination;
		}
		lastDist = dist;
		
		/*
		 * Exploding
		 */
		timeLeft -= deltaTime;
		if(timeLeft <= 0) {
			ArrayList<Collider> hit = Physics.checkCircle(pos, EXPLOSION_RADIUS);
			for(int i = 0; i < hit.size(); i++) {
				if(hit.get(i) instanceof Damagable) {
					((Damagable)hit.get(i)).damage(EXPLOSION_DAMAGE);
				}
			}
			new AnimatedEffect(Game.explosionTex, 
					new Vector(pos.x-EXPLOSION_RADIUS+SIZE.x/2, pos.y-EXPLOSION_RADIUS+SIZE.y/2), 
					new Vector(EXPLOSION_RADIUS*2, EXPLOSION_RADIUS*2), 
					Game.explosionTex.getDuration());
			new TextureEffect("crater", 
					new Vector(pos.x-EXPLOSION_RADIUS+SIZE.x/2, pos.y-EXPLOSION_RADIUS+SIZE.y/2), 
					new Vector(EXPLOSION_RADIUS*2, EXPLOSION_RADIUS*2), 
					20);
			destroy();
		}
		
	}
	
	@Override
	public void render() {
		
		glTranslated(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE, -0.6d);
		glColor3d(1, 1, 1);
		
		TexManager.bindTex("grenade");
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(0, 0);
		glVertex2d(0, size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 0);
		glVertex2d(size.x * Game.QUAD_SIZE, size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 1);
		glVertex2d(size.x * Game.QUAD_SIZE, 0);
		
		glTexCoord2d(0, 1);
		glVertex2d(0, 0);
		
		glEnd();
		
		glTranslated(-pos.x * Game.QUAD_SIZE, -pos.y * Game.QUAD_SIZE, 0.6d);
		
	}

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(pos);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		pos = (Vector) data.remove(0);
	}

	@Override
	public void onCollision(Collider hit) {
		if(hit.isBlocking && !(hit instanceof Player)) {
			velocity.x = 0;
			velocity.y = 0;
		}
	}

	@Override
	protected void onDestroy() {
		
	}

}
