package map;

import java.io.Serializable;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import loading.TexManager;
import main.Game;
import physics.Collider;
import physics.Physics;
import physics.Vector;

/**
 * 
 * This class represents a players spawn inside the world<br>
 * There may be only one of these for each player
 * 
 * @author jafi2
 *
 */
public class PlayerSpawn extends Collider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6174518532477958486L;
	
	/**
	 * The id of the player this spawn is intended for
	 */
	private int playerID = -1;
	
	/**
	 * Create a new spawn for a player
	 * @param pos the position of the spawn
	 * @param playerID the id of the player this spawn is intended for
	 */
	public PlayerSpawn(Vector pos, int playerID) {
		
		super(pos, new Vector(1,1), new Vector(), Physics.LAYER_WORLD | Physics.RAYCAST_IGNORE);
		isBlocking = false;
		this.playerID = playerID;
		
	}

	@Override
	public void update(double deltaTime) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render() {
		
		TexManager.bindTex("playerSpawn");
		
		if(playerID == 0) {
			glColor3d(1, 0, 0);
		} else if(playerID == 1) {
			glColor3d(0, 1, 0);
		} else if(playerID == 2) {
			glColor3d(0, 0, 1);
		} else if(playerID == 3) {
			glColor3d(1, 1, 0);
		}
		
		glTranslated(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE, -0.25d);
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(0, 0);
		glVertex2d(0, size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 0);
		glVertex2d(size.x * Game.QUAD_SIZE, size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 1);
		glVertex2d(size.x * Game.QUAD_SIZE, 0);
		
		glTexCoord2d(0, 1);
		glVertex2d(0, 0);
		
		glEnd();
		
		glTranslated(-pos.x * Game.QUAD_SIZE, -pos.y * Game.QUAD_SIZE, 0.25d);
		
	}

	@Override
	public void onCollision(Collider hit) {
		
	}

	@Override
	protected void onDestroy() {
		
	}
	
	/**
	 * Get the position of the spawn
	 * @return the position of the spawn
	 */
	public Vector getPosition() {
		return pos.clone();
	}
	
	/**
	 * get the id of the player this spawn is intended for
	 * @return the id of the player this spawn is intended for
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(pos);
		data.add(size);
		data.add(velocity);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		pos = (Vector) data.get(0);
		size = (Vector) data.get(1);
		velocity = (Vector) data.get(2);
	}
	
}
