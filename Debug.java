package debug;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import main.Game;
import physics.Ray;

public class Debug {

	public static ArrayList<Ray> rays = new ArrayList<>();
	
	public static void renderDebug() {
		
		glTranslated(0, 0, -10);
		
		for(int i = 0; i < rays.size(); i++) {
			
			Ray ray = rays.get(i);
			
			glLineWidth(4);
			glColor3d(1, 0, 0);
			
			glBegin(GL_LINES);
			
			glColor3d(0, 1, 0);
			glVertex2d(ray.origin.x * Game.QUAD_SIZE, ray.origin.y * Game.QUAD_SIZE);
			
			glColor3d(1, 0, 0);
			glVertex2d(ray.origin.x * Game.QUAD_SIZE + ray.dir.x * Game.QUAD_SIZE, ray.origin.y * Game.QUAD_SIZE + ray.dir.y * Game.QUAD_SIZE);
			
			glEnd();
			
		}
		
		glTranslated(0, 0, 10);
		
	}
	
	public static void drawRay(Ray ray) {
		rays.add(ray);
	}
	
}
