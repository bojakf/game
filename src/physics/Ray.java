package physics;

/**
 * 
 * represents a ray pointing from a specified point in a specified direction
 * 
 * @author jafi2
 *
 */
public class Ray {

	/**
	 * The origin of the ray
	 */
	public Vector origin;
	/**
	 * The direction of the ray
	 */
	public Vector dir;
	
	/**
	 * Create ray which uses default vectors
	 */
	public Ray() {
		origin = new Vector();
		dir = new Vector();
	}
	
	/**
	 * Create ray
	 * @param origin the origin of the ray
	 * @param dir the direction of the ray
	 */
	public Ray(Vector origin, Vector dir) {
		this.origin = origin;
		this.dir = dir;
	}
	
	/**
	 * print the ray
	 */
	public void print() {
		System.out.println("origin:\t" + origin.x + "\t" + origin.y);
		System.out.println("Direction:\t" + dir.x + "\t" + dir.y);
	}
	
	@Override
	public String toString() {
		return "origin:\t" + origin.x + "\t" + origin.y + "\nDirection:\t" + dir.x + "\t" + dir.y;
	}
	
}
