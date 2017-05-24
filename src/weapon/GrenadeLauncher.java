package weapon;

import input.Mouse;
import main.Game;
import main.Primitives;
import map.CameraController;
import physics.Vector;

import java.io.Serializable;
import java.util.ArrayList;

import components.Player;
import gameobject.Gameobject;
import ui.Ui;

/**
 * 
 * This weapon shoots projectiles exploding shortly after hitting the point clicked at by the player
 * 
 * @author jafi2
 *
 */
public class GrenadeLauncher extends Weapon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7112729492047046226L;

	/**
	 * The time required to reload
	 */
	private static final double RELOAD_TIME = 1;
	
	/**
	 * The player this weapon belongs to
	 */
	private Player player;
	/**
	 * The time since the last shot
	 */
	private double shotTime;
	
	/**
	 * Create a new Grenade Launcher
	 * @param player the player the weapon belongs to
	 */
	public GrenadeLauncher(Player player) {
		this.player = player;
	}
	
	@Override
	public void update(double deltaTime) {
		
		shotTime += deltaTime;
		
		if(shotTime >= RELOAD_TIME && Mouse.isMouseButtonDown(0, player.getPlayerID())) {
			shotTime = 0;
			Vector m = Mouse.xy(player.getPlayerID());
			m.x /= Game.QUAD_SIZE;
			m.y /= Game.QUAD_SIZE;
			
			Vector cam = CameraController.calcCamPos(player);
			m.x += cam.x-Game.WORLD_OFFSET_X;
			m.y += cam.y-Game.WORLD_OFFSET_Y;
			
			Gameobject grenade = Primitives.grenade.create(player.getParent().pos, Grenade.SIZE);
			((Grenade)grenade.getComponent(Grenade.class)).setDestination(m);
			grenade.init();
			
		}
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		
	}	
	
}
