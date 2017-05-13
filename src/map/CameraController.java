package map;

import java.util.ArrayList;

import components.Player;
import gameobject.Gameobject;
import main.Game;
import physics.Vector;

/**
 * 
 * Controller for the camera
 * 
 * @author jafi2
 *
 */
public class CameraController {

	public static double mapSizeX = 0;
	public static double mapSizeY = 0;
	
	private Player player = null;
	
	public void update(double deltaTime) {
		
		if(player == null) {
			ArrayList<Gameobject> g = Game.gameobjectsWith(Player.class);
			for(int i = 0; i < g.size(); i++) {
				Player p = (Player)g.get(i).getComponent(Player.class);
				if(p.getPlayerID() == Game.net.playerID) {
					player = p;
					break;
				}
			}
		} else {
			
			if(player.isDestroyed()) {
				player = null;
				return;
			}
			
			Vector v = calcCamPos(player);
			
			Game.camX = v.x;
			Game.camY = v.y;
			
		}
		
	}
	
	public static final Vector calcCamPos(Player player) {
		
		double x = player.getPosition().x - Game.QUADS_X*0.5;
		double y = player.getPosition().y - Game.QUADS_Y*0.5;
		
		x = Math.max(x, 0);
		y = Math.max(y, 0);
		x = Math.min(x, mapSizeX-Math.floor(Game.QUADS_X));
		y = Math.min(y, mapSizeY-Math.floor(Game.QUADS_Y));
		
		return new Vector(x, y);
		
	}
	
}
