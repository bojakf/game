package physics;

/**
 * 
 * Information return by physics.Physics.raycast method
 * 
 * @author jafi2
 *
 */
public class RaycastHit {

	/**
	 * The position of the hit object
	 */
	public Vector pos;
	/**
	 * The collider instance of the hit object
	 */
	public Collider hit;
	/**
	 * the distance the ray took to hit
	 */
	public double distance;
	
}
