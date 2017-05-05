package rendering.effects;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import loading.TexManager;
import main.Game;
import physics.Vector;

/**
 * 
 * A textureEffect displays a texture until the effect ends
 * 
 * @author jafi2
 *
 */
public class TextureEffect extends Effect {

	/*
	 * TODO fade at end of animation
	 */
	/*
	 * TODO fade in
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2622243764433748961L;
	
	/**
	 * The texture drawn
	 */
	private String texture;
	
	/**
	 * Create a new texture effect
	 * @param texture the texture of the effect
	 * @param pos the position of the effect
	 * @param size the size of the effect
	 * @param ttl the time until the effect ends
	 */
	public TextureEffect(String texture, Vector pos, Vector size, double ttl) {
		super(pos, size, ttl);
		this.texture = texture;
	}

	@Override
	public void render() {
		
		TexManager.bindTex(texture);
		
		glColor3d(1, 1, 1);
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

}
