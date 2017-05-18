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
import rendering.Color;

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
		
		if(playerID == 0) {
			((Renderer)parent.getComponent(Renderer.class)).col = new Color(1, 0, 0);
		} else if(playerID == 1) {
			((Renderer)parent.getComponent(Renderer.class)).col = new Color(0, 1, 0);
		} else if(playerID == 2) {
			((Renderer)parent.getComponent(Renderer.class)).col = new Color(0, 0, 1);
		} else if(playerID == 3) {
			((Renderer)parent.getComponent(Renderer.class)).col = new Color(1, 1, 0);
		}
				
	}

	@Override
	public void update(double deltaTime) {
		
	}

	@Override
	public void render() {
		
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
