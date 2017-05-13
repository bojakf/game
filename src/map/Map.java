package map;

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
public class Map {
	
	/**
	 * Create the map
	 */
	public Map() {
		
		for(int x = 0; x < Game.QUADS_X*2-1; x++) {
			for(int y = 0; y < Game.QUADS_Y*2; y++) {
				Primitives.mapFloor.create(new Vector(x, y)).init();
			}
		}
		
		for(int x = 0; x < Game.QUADS_X*2; x++) {
			for(int y = 0; y < Game.QUADS_Y*2; y++) {
				if(x == 0 || x == Math.floor(Game.QUADS_X*2) || y == 0 || y == Game.QUADS_Y*2-1) {
					Primitives.mapWall.create(new Vector(x, y)).init();
				}
			}
		}
		
		CameraController.mapSizeX = Game.QUADS_X * 2;
		CameraController.mapSizeY = Game.QUADS_Y * 2;
		
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
