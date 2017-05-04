package rendering;

import java.io.Serializable;

import org.lwjgl.opengl.GL11;

/**
 * 
 * Class representing a RGBA color
 * 
 * @author jafi2
 *
 */
public class Color implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4079717236626448908L;
	
	/**
	 * Color
	 */
	public double r, g, b, a;
	
	/**
	 * Create a new color. Colors range from 0.0 to 1.0
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha (1 = not transparent)
	 */
	public Color(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	/**
	 * Create a new color with no transparency. Colors range form 0.0 to 1.0
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public Color(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}
	
	/**
	 * Create a new color. The color will be white with no transparency
	 */
	public Color() {
		this.r = 1;
		this.g = 1;
		this.b = 1;
		this.a = 1;
	}
	
	/**
	 * use the color to render
	 */
	public void glColor() {
		GL11.glColor4d(r, g, b, a);
	}
	
}
