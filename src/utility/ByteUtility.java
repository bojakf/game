package utility;

import java.nio.ByteBuffer;

public class ByteUtility {

	public static int[] doubleToInts(double value) {
		
		byte bytes[] = new byte[8];
		int ints[] = new int[2];
		ByteBuffer.wrap(bytes).putDouble(value);
		
		ints[0] = ByteBuffer.wrap(bytes).getInt(0);
		ints[1] = ByteBuffer.wrap(bytes).getInt(4);
		
		return ints;
		
	}
	
	public static double intsToDouble(int value[]) {		
		
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putInt(value[0]);
		b.putInt(value[1]);
		
		b.rewind();
		
		return b.getDouble();
		
	}
	
}
