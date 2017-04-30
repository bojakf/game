package physics;

/**
 * 
 * This class should be used for objects which can be damaged
 * 
 * @author jafi2
 *
 */
public abstract class Damagable extends Collider {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8012449035845560440L;
	
	/**
	 * The health points of the object
	 */
	protected double hp;
	
	/**
	 * Init the damageable
	 * @param pos the position of the collider
	 * @param size the size of the collider
	 * @param velocity the velocity of the collider
	 * @param layer the physical layer of the collider
	 * @param hp the initial health points of the object
	 */
	public Damagable(Vector pos, Vector size, Vector velocity, int layer, double hp) {
		super(pos, size, velocity, layer);
		this.hp = hp;
	}

	/**
	 * Damage the object
	 * @param dmg the damage to inflict
	 * @return did the object get killed/destroyed
	 */
	public final boolean damage(double dmg) {
		hp -= dmg;
		if(hp < 0) {
			destroy();
			return true;
		}
		return false;
	}
	
}
