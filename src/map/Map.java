package map;

import static org.lwjgl.opengl.GL11.*;

import java.io.Serializable;
import java.util.ArrayList;

import loading.TexManager;

import static main.Game.QUAD_SIZE;

import main.Game;
import physics.Collider;
import physics.Physics;
import physics.Vector;

public class Map implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3678985439336866532L;

	private ArrayList<MapFloor> floor;
	private ArrayList<MapWall> walls;
	
	public Map() {
		
		floor = new ArrayList<>();
		walls = new ArrayList<>();
		
		for(int x = 0; x < Game.QUADS_X; x++) {
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
				if(x == 0 || x == Game.QUADS_X-1 || y == 0 || y == Game.QUADS_Y-1) {
					walls.add(new MapWall(new Vector(x, y), "wall"));
					Game.net.registerNetObject(walls.get(walls.size()-1));
				}
			}
		}
		
	}
	
	public void update(double deltaTime) {
		
	}
	
	public void render() {
	
		TexManager.bindTex("grass");
		
		glColor3d(1, 1, 1);
		glBegin(GL_QUADS);
		
		for(int x = 0; x < Game.QUADS_X; x++) {
			for(int y = 0; y < Game.QUADS_Y; y++) {
				
				glTexCoord2d(0, 0);
				glVertex2d(x * QUAD_SIZE, (y+1) * QUAD_SIZE);
				
				glTexCoord2d(1, 0);
				glVertex2d((x+1) * QUAD_SIZE, (y+1) * QUAD_SIZE);
				
				glTexCoord2d(1, 1);
				glVertex2d((x+1) * QUAD_SIZE, y * QUAD_SIZE);
				
				glTexCoord2d(0, 1);
				glVertex2d(x * QUAD_SIZE, y * QUAD_SIZE);
				
			}
		}
		
		glEnd();
		
	}
	
}
