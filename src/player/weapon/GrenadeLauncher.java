package player.weapon;

import java.io.Serializable;
import java.util.ArrayList;

import input.Mouse;
import main.Game;
import physics.Vector;
import player.Player;
import player.Weapon;
import ui.Ui;

/**
 * 
 * This weapon shoots projectiles exploding shortly after hitting the point clicked at by the player
 * 
 * @author jafi2
 *
 */
public class GrenadeLauncher implements Weapon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7112729492047046226L;

	/**
	 * The time required to reload
	 */
	private static final double RELOAD_TIME = 0.00001;
	
	/**
	 * The player this weapon belongs to
	 */
	private Player player;
	/**
	 * The time since the last shot
	 */
	private double shotTime = 0;
	
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
		
		if(shotTime >= RELOAD_TIME && Ui.isMouseButtonDown(0)) {
			shotTime = 0;
			Vector m = Mouse.xy(player.getPlayerID());
			m.x /= Game.QUAD_SIZE;
			m.y /= Game.QUAD_SIZE;
			m.x -= Grenade.SIZE.x/2;
			m.y -= Grenade.SIZE.y/2;
			new Grenade(Vector.add(player.getPosition(), new Vector(player.getSize().x/2, player.getSize().y/2)), m);
		}
		
	}

	@Override
	public void render() {
		
	}
	
	@Override
	public boolean isPendingDestroy() {
		return false;
	}
	
	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		
	}
	
	
}
