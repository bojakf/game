package map;

import java.io.Serializable;

import components.Spawn;
import gameobject.Gameobject;
import main.Game;
import main.Primitives;
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
	 * Create the map
	 */
	public Map() {
		
		for(int x = 0; x < Game.QUADS_X-1; x++) {
			for(int y = 0; y < Game.QUADS_Y; y++) {
				Primitives.mapFloor.create(new Vector(x, y)).init();
			}
		}
		
		for(int x = 0; x < Game.QUADS_X; x++) {
			for(int y = 0; y < Game.QUADS_Y; y++) {
				if(x == 0 || x == Math.floor(Game.QUADS_X) || y == 0 || y == Game.QUADS_Y-1) {
					Primitives.mapWall.create(new Vector(x, y)).init();
				}
			}
		}
		
		Gameobject s = Primitives.spawn.create(new Vector(2, 2));
		((Spawn)s.getComponent(Spawn.class)).playerID = 0;
		s.init();
		s = Primitives.spawn.create(new Vector(2, Game.QUADS_Y-3));
		((Spawn)s.getComponent(Spawn.class)).playerID = 1;
		s.init();
		s = Primitives.spawn.create(new Vector((int)Game.QUADS_X-2, Game.QUADS_Y-3));
		((Spawn)s.getComponent(Spawn.class)).playerID = 2;
		s.init();
		s = Primitives.spawn.create(new Vector((int)Game.QUADS_X-2, 2));
		((Spawn)s.getComponent(Spawn.class)).playerID = 3;
		s.init();
		
	}
	
}
