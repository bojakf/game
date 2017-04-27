package utility;

import java.nio.ByteBuffer;

/**
 * 
 * utility for byteBuffers
 * 
 * @author jafi2
 *
 */
public class ByteUtility {

	/**
	 * Convert double to 2 ints in order to be able to perform bitwise operations on it
	 * @param value the double to convert
	 * @return the converted integers
	 */
	public static int[] doubleToInts(double value) {
		
		byte bytes[] = new byte[8];
		int ints[] = new int[2];
		ByteBuffer.wrap(bytes).putDouble(value);
		
		ints[0] = ByteBuffer.wrap(bytes).getInt(0);
		ints[1] = ByteBuffer.wrap(bytes).getInt(4);
		
		return ints;
		
	}
	
	/**
	 * Reverse method of the above one
	 * @param value the integers to convert back into a double
	 * @return the double
	 */
	public static double intsToDouble(int value[]) {		
		
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putInt(value[0]);
		b.putInt(value[1]);
		
		b.rewind();
		
		return b.getDouble();
		
	}
	
}
