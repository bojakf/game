package utility;

public class Mathd {

	/**
	 * 
	 * get the rotation Matrix for the angles
	 * angles in degree
	 * 
	 * @param rotX
	 * @param rotY
	 * @param rotZ
	 * @return
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
	 * @param angel
	 * @return
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
	 * @param A
	 * @param B
	 * @return
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
	
	public static double clamp(double v, double min, double max) {
		
		if(v < min) {
			return min;
		}
		if(v > max) {
			return max;
		}
		return v;
		
	}
	
	public static double roundTo(double v, long target) {
		if(v < target) return Math.ceil(v);
		if(v > target) return Math.floor(v);
		return v;
	}
	
	public static double randomRange(double min, double max) {
		return min+(max-min)*Math.random();
	}
	
	public static int ggt(int zahl1, int zahl2) {
		while (zahl2 != 0) {
		   if (zahl1 > zahl2) {
			   zahl1 = zahl1 - zahl2;
		   } else {
			   zahl2 = zahl2 - zahl1;
		   }
		}
		return zahl1;
	}
	
}
