package main;

import components.Damageable;
import components.Effect;
import components.FinalRenderer;
import components.HealthPack;
import components.Player;
import components.Renderer;
import components.Spawn;
import gameobject.Primitive;
import loading.TexManager;
import mapCreator.MapCreator;
import physics.Collider;
import physics.Physics;
import weapon.Grenade;
import weapon.Rocket;

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
	private Primitives() {}
	
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
	/**
	 * primitive for a healthPack
	 */
	public static final Primitive healthPack;
	/**
	 * primitive for a rocket
	 */
	public static final Primitive rocket;
	
	static {
		
		mapWall = new Primitive(Game.L_WALL);
		mapWall.addComponent(FinalRenderer.class, "wall");
		mapWall.addComponent(Collider.class, Physics.LAYER_WORLD);
		MapCreator.mapObjects.add(mapWall);
		
		mapFloor = new Primitive(Game.L_FLOOR);
		mapFloor.addComponent(FinalRenderer.class, "grass");
		MapCreator.mapObjects.add(mapFloor);
		
		player = new Primitive(Game.L_PLAYER);
		player.addComponent(Renderer.class, TexManager.getAnimation("player"));
		player.addComponent(Collider.class, Physics.LAYER_PLAYER, true, false);
		player.addComponent(Player.class);
		
		spawn = new Primitive(Game.L_DECO);
		spawn.addComponent(Collider.class, Physics.LAYER_WORLD | Physics.RAYCAST_IGNORE);
		spawn.addComponent(Spawn.class);
		spawn.addComponent(Renderer.class, "playerSpawn");
		MapCreator.mapObjects.add(spawn);
		
		grenade = new Primitive(Game.L_WEAPONS);
		grenade.addComponent(Collider.class, Physics.LAYER_ENEMY | Physics.RAYCAST_IGNORE);
		grenade.addComponent(Grenade.class);
		grenade.addComponent(Renderer.class, "grenade");
		
		explosion = new Primitive(Game.L_EFFECT_1);
		explosion.addComponent(Effect.class, TexManager.getAnimation("explosion"));
		
		crater = new Primitive(Game.L_EFFECT_2);
		crater.addComponent(Effect.class, "crater", 20d);
		
		healthPack = new Primitive(Game.L_WEAPONS);
		healthPack.addComponent(Renderer.class, "healthPack");
		healthPack.addComponent(HealthPack.class);
		healthPack.addComponent(Collider.class, Physics.LAYER_WORLD | Physics.RAYCAST_IGNORE, false, true);
		MapCreator.mapObjects.add(healthPack);
		
		rocket = new Primitive(Game.L_WEAPONS);
		rocket.addComponent(Renderer.class, "rocket");
		rocket.addComponent(Collider.class, Physics.LAYER_DEFAULT);
		rocket.addComponent(Damageable.class, 1d);
		rocket.addComponent(Rocket.class);
		
	}
	
}
