package debug;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import main.Game;
import physics.Vector;

/**
 * 
 * Used for debugging
 * 
 * @author jafi2
 *
 */
public class Debug {
	
	/**
	 * lines to render in renderDebug()
	 */
	public static ArrayList<Vector> lines = new ArrayList<>();
	
	/**
	 * Called when debug should be rendered (mainly in main.Game.render())
	 */
	public static void renderDebug() {
		
		glTranslated(0, 0, -10);
		
		glBegin(GL_LINES);
		for(int i = 0; i < lines.size(); i++) {
			
			Vector v = lines.get(i);
			
			glLineWidth(4);
			
			if(i%2 == 0) glColor3d(0, 1, 0); else glColor3d(1, 0, 0);
			glVertex2d(v.x * Game.QUAD_SIZE, v.y * Game.QUAD_SIZE);
			
		}
		
		glEnd();
		
		glTranslated(0, 0, 10);
		
	}
	
	/**
	 * Adds line to draw
	 * @param start start of the line
	 * @param end end of the line
	 */
	public static void drawLine(Vector start, Vector end) {
		lines.add(start);
		lines.add(end);
	}
	
}
