package utility;

import java.util.Random;

public class SRandom extends Random {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long seed;
	
	public SRandom() {
		
		this(System.currentTimeMillis());
		
	}
	
	public SRandom(long seed) {
		
		super(seed);
		this.seed = seed;
		
	}
	
	public long getSeed() {
		return seed;
	}
	
}
