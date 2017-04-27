package utility;

/**
 * 
 * A generic class for handling callbacks
 * 
 * @author jafi2
 *
 */
public abstract class Callback {
	/**
	 * generic callback method
	 * @param info information given by the caller
	 * @return information sent back to the caller
	 */
	public abstract Object[] inkove(Object[] info);
}
