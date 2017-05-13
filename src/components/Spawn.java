package components;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.io.Serializable;
import java.util.ArrayList;

import loading.TexManager;
import main.Game;
import physics.Collider;
import physics.Vector;

/**
 * 
 * This class represents a players spawn inside the world<br>
 * There may be only one of these for each player
 * 
 * @author jafi2
 *
 */
public class Spawn extends NetComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7096459538523413298L;

	/**
	 * The id of the player this spawn belongs to
	 */
	public int playerID = -1;
	
	@Override
	public void start() {
		
		super.start();
		
		((Collider)parent.getComponent(Collider.class)).isBlocking = false;
		
	}

	@Override
	public void update(double deltaTime) {
		
	}

	@Override
	public void render() {
		
		if(playerID == 0) {
			glColor3d(1, 0, 0);
		} else if(playerID == 1) {
			glColor3d(0, 1, 0);
		} else if(playerID == 2) {
			glColor3d(0, 0, 1);
		} else if(playerID == 3) {
			glColor3d(1, 1, 0);
		}
		
		TexManager.bindTex("playerSpawn");
		
		double x = Game.QUAD_SIZE*parent.size.x*0.5d;
		double y = Game.QUAD_SIZE*parent.size.y*0.5d;
		
		glTranslated(parent.pos.x*Game.QUAD_SIZE, parent.pos.y*Game.QUAD_SIZE, 0);
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(0, 0);
		glVertex2d(-x, y);
		
		glTexCoord2d(1, 0);
		glVertex2d(x, y);
		
		glTexCoord2d(1, 1);
		glVertex2d(x, -y);
		
		glTexCoord2d(0, 1);
		glVertex2d(-x, -y);
		
		glEnd();
		
		glTranslated(-(parent.pos.x*Game.QUAD_SIZE), -(parent.pos.y*Game.QUAD_SIZE), 0);
		
	}

	@Override
	protected void onDestroy() {
		
	}

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(parent.pos);
		data.add(parent.size);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		parent.pos.set((Vector)data.remove(0));
		parent.size.set((Vector)data.remove(0));
	}

}
