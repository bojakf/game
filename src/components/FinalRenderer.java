package components;

import static org.lwjgl.opengl.GL11.*;

import loading.TexManager;
import main.Game;
import rendering.Color;

/**
 * 
 * Component for rendering basic textures.
 * A Gameobject with this component may not be moved as the movement will not be sent to clients
 * 
 * @author jafi2
 *
 */
public class FinalRenderer extends FinalNetComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6949728512768379374L;
	
	/**
	 * The name of the texture to draw.
	 */
	private String texName = null;
	
	/**
	 * the color the render uses to render
	 */
	public Color col = new Color();
	
	/**
	 * Create a new Renderer Component with a texture
	 * @param texName the texture
	 */
	public FinalRenderer(String texName) {
		this.texName = texName;
	}
	
	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void update(double deltaTime) {
		
	}

	@Override
	public void render() {
		
		TexManager.bindTex(texName);
		
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

}
