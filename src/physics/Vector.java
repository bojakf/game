package physics;

import java.io.Serializable;

/**
 * 
 * Just a Vector
 * 
 * @author jafi2
 *
 */
public class Vector implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7583292376820685901L;
	
	
	/**
	 * x value of vector
	 */
	public double x=0;
	/**
	 * y value of vector
	 */
	public double y=0;
	
	/**
	 * Create Vector using specified values
	 * @param x the x value
	 * @param y the y value
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Create Vector using x = 0 and y = 0
	 */
	public Vector() {}
	
	/**
	 * normalize the vector
	 */
	public void normalize() {
		double l = length();
		if(l != 0) {
			x /= l;
			y /= l;
		}
	}
	
	/**
	 * Set the values of this vector equal to the ones from the vector provided
	 * @param v the vector provided
	 */
	public void set(Vector v) {
		x = v.x;
		y = v.y;
	}
	
	/**
	 * get the length of the vector
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt(x*x+y*y);
	}
	
	/**
	 * scale/multiply the vector with a value
	 * @param factor the scale value
	 */
	public void scale(double factor) {
		x *= factor;
		y *= factor;
	}
	
	/**
	 * Add another Vector to this one. Only this vector is modified
	 * @param v the other vector
	 */
	public void add(Vector v) {
		x += v.x;
		y += v.y;
	}
	
	/**
	 * Subtract another Vector from this one. Only this vector is modified
	 * @param v the other vector
	 */
	public void subtract(Vector v) {
		x -= v.x;
		y -= v.y;
	}
	
	/**
	 * Add two vectors and return the result in a new one. None of the two vectors will be modified
	 * @param a the first vector
	 * @param b the second vector
	 * @return the addition of the two vectors
	 */
	public static Vector add(Vector a, Vector b) {
		return new Vector(a.x+b.x, a.y+b.y);
	}
	
	/**
	 * Subtract one vector form another and return the result in a new one. None of the two vectors will be modified
	 * @param a the vector to subtract from
	 * @param b the other vector
	 * @return the subtraction of the two vectors
	 */
	public static Vector substract(Vector a, Vector b) {
		return new Vector(a.x-b.x, a.y-b.y);
	}
	
	@Override
	public String toString() {
		return "x: " + x + "      y: " + y;
	}
	
	@Override
	public Vector clone() {
		return new Vector(x, y);
	}
	
}
