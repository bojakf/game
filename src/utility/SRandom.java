package utility;

import java.util.Random;

/**
 * 
 * A random object of which the seed is saved.<br>
 * By saving the seed and creating a new object with the saved seed results can be reproduced
 * 
 * @author jafi2
 *
 */
public class SRandom extends Random {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1608253410475018729L;
	
	/**
	 * The seed of the object
	 */
	private long seed;
	
	/**
	 * Create the object by automatically generating a seed form the current time
	 */
	public SRandom() {
		
		this(System.currentTimeMillis());
		
	}
	
	/**
	 * Initialize the object with a seed
	 * @param seed the seed which should be used
	 */
	public SRandom(long seed) {
		
		super(seed);
		this.seed = seed;
		
	}
	
	/**
	 * Returns the seed of the object
	 * @return the seed
	 */
	public long getSeed() {
		return seed;
	}
	
}
