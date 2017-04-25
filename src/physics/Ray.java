package physics;

public class Ray {

	public Vector origin;
	public Vector dir;
	
	public Ray() {}
	
	public Ray(Vector origin, Vector dir) {
		this.origin = origin;
		this.dir = dir;
	}
	
	public void print() {
		System.out.println("origin:\t" + origin.x + "\t" + origin.y);
		System.out.println("Direction:\t" + dir.x + "\t" + dir.y);
	}
	
}
