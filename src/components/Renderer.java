package components;

import static org.lwjgl.opengl.GL11.*;

import java.io.Serializable;
import java.util.ArrayList;

import loading.AnimatedTexture;
import loading.TexManager;
import main.Game;
import physics.Vector;
import rendering.Color;

/**
 * 
 * Component for rendering basic textures and animations
 * 
 * @author jafi2
 *
 */
public class Renderer extends NetComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5973967665127368837L;
	
	/**
	 * The name of the texture to draw.
	 * null if an animation is used
	 */
	private String texName = null;
	/**
	 * The animated texture to draw.
	 * null if a texture is used
	 */
	private AnimatedTexture tex = null;
	
	/**
	 * the color the render uses to render
	 */
	public Color col = new Color();
	
	/**
	 * Create a new Renderer Component with a texture
	 * @param texName the texture
	 */
	public Renderer(String texName) {
		this.texName = texName;
	}
	
	/**
	 * Create a new Renderer Component with an animation
	 * @param tex the animation
	 */
	public Renderer(AnimatedTexture tex) {
		this.tex = tex.clone();
	}
	
	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void update(double deltaTime) {
		if(tex != null) {
			tex.update(deltaTime);
		}
	}

	@Override
	public void render() {
		
		if(tex == null) {
			TexManager.bindTex(texName);
		} else {
			tex.bindCur();
		}
		
		double x = Game.QUAD_SIZE*parent.size.x*0.5d;
		double y = Game.QUAD_SIZE*parent.size.y*0.5d;
		
		col.glColor();
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
	public void onDestroy() {
		
	}

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(parent.pos);
		data.add(parent.size);
		if(tex != null) {
			data.add((Integer)tex.curTex);
		}
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		parent.pos.set((Vector)data.remove(0));
		parent.size.set((Vector)data.remove(0));
		if(tex != null) {
			 tex.curTex = (int) data.remove(0);
		}
	}

	public String getTex() {
		return texName;
	}

	public AnimatedTexture getAnimation() {
		return tex;
	}
	
	public void setTex(String tex) {
		this.texName = tex;
	}
	
	public void setAnimation(AnimatedTexture animation) {
		this.tex = animation;
	}

}
