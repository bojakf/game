package rendering.effects;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.io.Serializable;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.glColor3d;

import main.Game;
import physics.Vector;
import rendering.AnimatedTexture;

/**
 * 
 * An animatedEffect displays a animated texture until the effect ends
 * 
 * @author jafi2
 *
 */
public class AnimatedEffect extends Effect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1630648489441600921L;
	
	/**
	 * The animation used by the effect
	 */
	private AnimatedTexture texture;
	
	public AnimatedEffect(AnimatedTexture texture, Vector pos, Vector size, double ttl) {
		super(pos, size, ttl);
		this.texture = texture.clone();
	}

	@Override
	public void update(double deltaTime) {
		texture.update(deltaTime);
		super.update(deltaTime);
	}
	
	@Override
	public void render() {
		
		/*
		 * This is needed for animations because the first frame would be shown
		 * again for a short time before the effect ends
		 */
		if(ttl<=0) return;
		
		glColor3d(1, 1, 1);
		texture.bindCur();
		
		glTranslated(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE, -0.1d);
		
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
		
		glTranslated(-pos.x * Game.QUAD_SIZE, -pos.y * Game.QUAD_SIZE, 0.1d);
		
	}
	
	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		super.sendNetUpdate(data);
		data.add(texture.curTex);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		super.receiveNetUpdate(data);
		texture.curTex = (int) data.remove(0);
	}
	
}
