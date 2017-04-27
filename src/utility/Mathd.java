package utility;

/**
 * 
 * Class for additional Math implementations
 * 
 * @author jafi2
 *
 */
public class Mathd {

	/**
	 * 
	 * get the rotation Matrix for the angles
	 * angles in degree
	 * 
	 * @param rotX the x rotation
	 * @param rotY the y rotation
	 * @param rotZ the z rotation
	 * @return the rotation matrix
	 */
	public static double[][] getRotationMatrix(double rotX, double rotY, double rotZ) {
		
		rotX = Math.toRadians(rotX);
		rotY = Math.toRadians(rotY);
		rotZ = Math.toRadians(rotZ);
		
		double x[][] = {
				{1,		0					,0},
				{0, 	Math.cos(rotX), 	Math.sin(rotX)},
				{0, 	-Math.sin(rotX), 	Math.cos(rotX)}
		};
		
		double y[][] = {
				{Math.cos(rotY), 	0, 		-Math.sin(rotY)},
				{0, 				1, 		0},
				{Math.sin(rotY), 	0, 		Math.cos(rotY)}
		};
		
		double z[][] = {
				{Math.cos(rotZ), 	Math.sin(rotZ), 0},
				{-Math.sin(rotZ), 	Math.cos(rotZ), 0},
				{0,					0,				1}
		};
		
		return matMul(matMul(x, y), z);
		
	}
	
	/**
	 * Clamp angle between 0 and 360
	 * (without changing it's actual direction)
	 * @param angle the angle to clamp
	 * @return the clamped angle
	 */
	public static double clampAngle(double angle) {
		while(angle < 0) {
			angle += 360;
		}
		while(angle >= 360) {
			angle -= 360;
		}
		return angle;
	}
	
	/**
	 * 
	 * Multiply matrixes
	 * 
	 * @param A the left hand side matrix
	 * @param B the right hand side matrix
	 * @return the multiplication of the matrixes
	 */
	
	public static double[][] matMul(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
	
	/**
	 * Clamp a value between two others<br>
	 * <b>Warning: </b> does not work if min is bigger then max
	 * @param v the value to be clamped
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return the clamped value
	 */
	public static double clamp(double v, double min, double max) {
		
		if(v < min) {
			return min;
		}
		if(v > max) {
			return max;
		}
		return v;
		
	}
	
	/**
	 * The number will be rounded. The number will always get closer to the target
	 * @param v the value
	 * @param target the target value
	 * @return the rounded value
	 */
	public static double roundTo(double v, long target) {
		if(v < target) return Math.ceil(v);
		if(v > target) return Math.floor(v);
		return v;
	}
	
	/**
	 * Returns a random number between min and max
	 * @param min the minimum number
	 * @param max the maximum number
	 * @return the number generated
	 */
	public static double randomRange(double min, double max) {
		return min+(max-min)*Math.random();
	}
	
	/**
	 * Calculates the greatest common divisor
	 * @param num1 the first number
	 * @param num2 the second number
	 * @return the greatest common divisor
	 */
	public static int gcd(int num1, int num2) {
		while (num2 != 0) {
		   if (num1 > num2) {
			   num1 = num1 - num2;
		   } else {
			   num2 = num2 - num1;
		   }
		}
		return num1;
	}
	
}
