package map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

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
	
	/*
	 * TODO option to chose map
	 */
	public static String mapName = "test";
	
	/**
	 * Create the map
	 */
	public Map() {
		
		double minX = 0;
		double minY = 0;
		double maxX = 0;
		double maxY = 0;
		
		try {
			
			File map = new File(Game.gamePath + "maps\\" + mapName + ".sav");
			if(!map.exists()) {
				new Exception("Map not found: " + map.getAbsolutePath() + "\nCreating default map").printStackTrace();
				createDefault();
				return;
			}
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(map));
			
			int count = in.readInt();
			
			Gameobject[] arr = new Gameobject[count];
			for(int i = 0; i < count; i++) {
				arr[i] = (Gameobject) in.readObject();
				minX = Math.min(minX, arr[i].pos.x);
				minY = Math.min(minY, arr[i].pos.y);
				maxX = Math.max(maxX, arr[i].pos.x);
				maxY = Math.max(maxY, arr[i].pos.y);
				arr[i].init();
			}
			CameraController.mapSizeX = maxX - minX;
			CameraController.mapSizeY = maxY - minY;
			CameraController.mapX = minX;
			CameraController.mapY = minY;
			
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load map");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load map");
		}
		
		
		
	}
	
	private void createDefault() {
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
		
		s = Primitives.healthPack.create(new Vector((int)Game.QUADS_X/2, (int)Game.QUADS_Y/2));
		s.init();
		
	}
	
}
