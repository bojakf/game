package physics;

import java.io.Serializable;

public class Vector implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7583292376820685901L;
	
	
	public double x=0, y=0;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector() {}
	
	public void normalize() {
		double l = length();
		x /= l;
		y /= l;
	}
	
	public double length() {
		return Math.sqrt(x*x+y*y);
	}
	
	public void scale(double factor) {
		x *= factor;
		y *= factor;
	}
	
	public void add(Vector v) {
		x += v.x;
		y += v.y;
	}
	
	public void substract(Vector v) {
		x -= v.x;
		y -= v.y;
	}
	
	public static Vector add(Vector a, Vector b) {
		return new Vector(a.x+b.x, a.y+b.y);
	}
	
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
