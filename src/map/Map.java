package map;

import java.io.Serializable;
import java.util.ArrayList;

import main.Game;
import physics.Vector;

/**
 * 
 * Does not do anything else then creating the map
 * 
 * @author jafi2
 *
 */
public class Map implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3678985439336866532L;

	/**
	 * The floor
	 */
	private ArrayList<MapFloor> floor;
	/**
	 * the wals
	 */
	private ArrayList<MapWall> walls;
	
	/**
	 * Create the map
	 */
	public Map() {
		
		floor = new ArrayList<>();
		walls = new ArrayList<>();
		
		for(int x = 0; x < Game.QUADS_X-1; x++) {
			for(int y = 0; y < Game.QUADS_Y; y++) {
				if(x == 5 && y == 5) {
					floor.add(new MapFloor(new Vector(x, y), new Vector(2, 2), "grass"));
					Game.net.registerNetObject(floor.get(floor.size()-1));
				} else if ((x == 5 || x == 6) && (y == 5 || y == 6)) {
					
				} else {
					floor.add(new MapFloor(new Vector(x, y), "grass"));
					Game.net.registerNetObject(floor.get(floor.size()-1));
				}
			}
		}
		
		for(int x = 0; x < Game.QUADS_X; x++) {
			for(int y = 0; y < Game.QUADS_Y; y++) {
				if(x == 0 || x == Math.floor(Game.QUADS_X) || y == 0 || y == Game.QUADS_Y-1) {
					walls.add(new MapWall(new Vector(x, y), "wall"));
					Game.net.registerNetObject(walls.get(walls.size()-1));
				}
			}
		}
		
	}
	
}
