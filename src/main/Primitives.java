package main;

import components.Effect;
import components.Player;
import components.Renderer;
import components.Spawn;
import gameobject.Primitive;
import loading.TexManager;
import physics.Collider;
import physics.Physics;
import player.weapon.Grenade;

/**
 * 
 * Contains primitives for frequently used gameobjects
 * 
 * @author jafi2
 *
 */
public class Primitives {
	
	/**
	 * Contains only static fields
	 */
	public Primitives() {}
	
	/**
	 * primitive for a map wall
	 */
	public static final Primitive mapWall;
	/**
	 * primitive for a map floor
	 */
	public static final Primitive mapFloor;
	/**
	 * primitive for a player
	 */
	public static final Primitive player;
	/**
	 * primitive for a spawn
	 */
	public static final Primitive spawn;
	/**
	 * primitive for a grenade
	 */
	public static final Primitive grenade;
	/**
	 * primitive for a explosion
	 */
	public static final Primitive explosion;
	/**
	 * primitive for a crater
	 */
	public static final Primitive crater;
	
	static {
		
		mapWall = new Primitive(Game.L_WALL);
		mapWall.addComponent(Renderer.class, "wall");
		mapWall.addComponent(Collider.class, Physics.LAYER_WORLD);
		
		mapFloor = new Primitive(Game.L_FLOOR);
		mapFloor.addComponent(Renderer.class, "grass");
		
		player = new Primitive(Game.L_PLAYER);
		player.addComponent(Renderer.class, TexManager.getAnimation("player"));
		player.addComponent(Collider.class, Physics.LAYER_PLAYER, true, false);
		player.addComponent(Player.class);
		
		spawn = new Primitive(Game.L_DECO);
		spawn.addComponent(Collider.class, Physics.LAYER_WORLD | Physics.RAYCAST_IGNORE);
		spawn.addComponent(Spawn.class);
		
		grenade = new Primitive(Game.L_WEAPONS);
		grenade.addComponent(Collider.class, Physics.LAYER_ENEMY | Physics.RAYCAST_IGNORE);
		grenade.addComponent(Grenade.class);
		grenade.addComponent(Renderer.class, "grenade");
		
		explosion = new Primitive(Game.L_EFFECT_1);
		explosion.addComponent(Effect.class, TexManager.getAnimation("explosion"));
		
		crater = new Primitive(Game.L_EFFECT_2);
		crater.addComponent(Effect.class, "crater", 20d);
		
	}
	
}
