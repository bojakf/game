package map;

import java.io.Serializable;
import java.util.ArrayList;

import loading.TexManager;
import main.Game;
import network.NetObject;

import static org.lwjgl.opengl.GL11.*;

import physics.Vector;

public class MapFloor implements Serializable, NetObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2662505268384273738L;
	
	/*
	 * TODO use this everywhere
	 */
	private final boolean finalPos = false;
	private final boolean finalSize = false;
	private final boolean finalTex = false;
	
	private Vector pos, size;
	private String tex;
	
	public MapFloor(Vector pos, String tex) {
		this.pos = pos;
		this.size = new Vector(1, 1);
		this.tex = tex;
	}
	
	public MapFloor(Vector pos, Vector size, String tex) {
		this.pos = pos;
		this.size = size;
		this.tex = tex;
	}
	
	@Override
	public void update(double deltaTime) {
		// TODO Auto-generated method stub
		
	}
	
	public void render() {
		
		glTranslated(0, 0, 1);
		
		glColor3d(1, 1, 1);
		TexManager.bindTex(tex);
		
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
		
		glTranslated(0, 0, -1);
		
	}

	@Override
	public void sendNetUpdate(ArrayList<Object> data) {
		//FIXME don't do this
//		data.add(pos);
//		data.add(size);
//		data.add(tex);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Object> data) {
		//FIXME don't do this
//		pos = (Vector) data.get(0);
//		size = (Vector) data.get(1);
//		tex = (String) data.get(2);
	}

}
