package map;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.io.Serializable;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import loading.TexManager;
import main.Game;
import network.NetObject;
import physics.Collider;
import physics.Physics;
import physics.Vector;

public class MapWall extends Collider implements Serializable, NetObject {

	/*
	 * FIXME this object does not do what it's supposed to do
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5700843973237153515L;
	
	private String tex;
	
	public MapWall(Vector pos, String tex) {
		super(pos, new Vector(1, 1), new Vector(), Physics.LAYER_WORLD);
		this.tex = tex;
	}
	
	public MapWall(Vector pos, Vector size, String tex) {
		super(pos, size, new Vector(), Physics.LAYER_WORLD);
		this.tex = tex;
	}
	
	@Override
	public void update(double deltaTime) {
		// TODO Auto-generated method stub
		
	}
	
	public void render() {
		
		glTranslated(0, 0, -1);
		
		glColor3d(1, 1, 1);
		TexManager.bindTex("wall");
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(0, 0);
		glVertex2d(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 0);
		glVertex2d(pos.x * Game.QUAD_SIZE + size.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 1);
		glVertex2d(pos.x * Game.QUAD_SIZE + size.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE);
		
		glTexCoord2d(0, 1);
		glVertex2d(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE);
		
		
		glEnd();
		
		glTranslated(0, 0, 1);
		
	}

	@Override
	public void onCollision(Collider hit) {
		
	}

	@Override
	protected void onDestroy() {
		
	}

	@Override
	public Object[] getHitInfo() {
		return null;
	}

	@Override
	public void sendNetUpdate(ArrayList<Object> data) {
		//FIXME don't do this
//		data.add(pos);
//		data.add(size);
//		data.add(velocity);
//		data.add(tex);
	}
	
	@Override
	public void receiveNetUpdate(ArrayList<Object> data) {
		//FIXME don't do this
//		pos = (Vector) data.get(0);
//		size = (Vector) data.get(1);
//		velocity = (Vector) data.get(2);
//		tex = (String) data.get(3);
	}
	
}
