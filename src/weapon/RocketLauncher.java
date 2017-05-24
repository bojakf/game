package weapon;

import java.io.Serializable;
import java.util.ArrayList;

import components.Damageable;
import components.Player;
import gameobject.Gameobject;
import input.Mouse;
import main.Game;
import main.Primitives;
import map.CameraController;
import physics.Vector;

public class RocketLauncher extends Weapon {

	/*
	 * TODO (follow nearest damageable) or (remove following completely)
	 */
	
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
	
	public RocketLauncher(Player player) {
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
			
			Gameobject rocket = Primitives.rocket.create(player.getParent().pos, Rocket.SIZE);
			Rocket r = (Rocket)rocket.getComponent(Rocket.class);
			
			boolean dest = false;
			
			for(Gameobject g : Game.getGameobjects()) {
				if(g.hasComponent(Damageable.class) && checkCursorInside(m, g.pos, g.size) 
						&& !(g.hasComponent(Player.class) && (Player)g.getComponent(Player.class) == player)) {
					r.setTarget(g);
					dest = true;
					break;
				}
			}
			
			if(!dest) {
				r.setDest(m);
			}
			r.setPlayer(player);
			
			rocket.init();
			
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
	
	private boolean checkCursorInside(Vector c, Vector pos, Vector size) {
		if(c.x < pos.x-size.x*0.5) return false;
		if(c.x > pos.x+size.x*0.5) return false;
		if(c.y < pos.y-size.y*0.5) return false;
		if(c.y > pos.y+size.y*0.5) return false;
		return true;
	}

}
