package rendering;

import static org.lwjgl.opengl.GL11.*;

import loading.TexManager;

/**
 * 
 * Used to render text
 * 
 * @author jafi2
 *
 */
public class StringDrawer {
	
	/**
	 * The width of a letter. May be modified
	 */
	public static double letterWidth = 12;
	/**
	 * The height of a letter. May be modified
	 */
	public static double letterHeight = 12;
	/**
	 * Used for texture coordinates
	 */
	private static final double COORD = 1d / 16d;
	
	/**
	 * This class contains only static methods
	 */
	private StringDrawer() {}
	
	/**
	 * Get the width a text would have
	 * @param text the text
	 * @return the expected with
	 */
	public static double getWidth(String text) {
		return letterWidth * text.length();
	}
	
	/**
	 * draw a string
	 * @param text the string
	 * @param x the x position of the start of the string
	 * @param y the y position of the string
	 */
	public static void drawString(String text, double x, double y) {
		
		text = text.toUpperCase();
		double xOff = x;
		for(int i = 0; i < text.length(); i++) {
			drawLetter(xOff, y, text.charAt(i));
			xOff += letterWidth;
		}
		
	}
	
	/**
	 * draw a string
	 * @param text the string
	 * @param x the x position of the middle of the string
	 * @param y the y position of the middle of the string
	 */
	public static void drawStringCentered(String text, double x, double y) {
		drawString(text, x-getWidth(text)/2, y-letterHeight/2);
	}
	
	private static void drawLetter(double x, double y, char letter) {
		
		TexManager.bindTex("ascii");
		
		double s = 0, t = 0;
		
		switch (letter) {
		case ' ':
			return;
		case 'A':
			s = 1;
			t = 4;
			break;
		case 'B':
			s = 2;
			t = 4;
			break;
		case 'C':
			s = 3;
			t = 4;
			break;
		case 'D':
			s = 4;
			t = 4;
			break;
		case 'E':
			s = 5;
			t = 4;
			break;
		case 'F':
			s = 6;
			t = 4;
			break;
		case 'G':
			s = 7;
			t = 4;
			break;
		case 'H':
			s = 8;
			t = 4;
			break;
		case 'I':
			s = 9;
			t = 4;
			break;
		case 'J':
			s = 10;
			t = 4;
			break;
		case 'K':
			s = 11;
			t = 4;
			break;
		case 'L':
			s = 12;
			t = 4;
			break;
		case 'M':
			s = 13;
			t = 4;
			break;
		case 'N':
			s = 14;
			t = 4;
			break;
		case 'O':
			s = 15;
			t = 4;
			break;
		case 'P':
			s = 0;
			t = 5;
			break;
		case 'Q':
			s = 1;
			t = 5;
			break;
		case 'R':
			s = 2;
			t = 5;
			break;
		case 'S':
			s = 3;
			t = 5;
			break;
		case 'T':
			s = 4;
			t = 5;
			break;
		case 'U':
			s = 5;
			t = 5;
			break;
		case 'V':
			s = 6;
			t = 5;
			break;
		case 'W':
			s = 7;
			t = 5;
			break;
		case 'X':
			s = 8;
			t = 5;
			break;
		case 'Y':
			s = 9;
			t = 5;
			break;
		case 'Z':
			s = 10;
			t = 5;
			break;
		case '0':
			s = 0;
			t = 3;
			break;
		case '1':
			s = 1;
			t = 3;
			break;
		case '2':
			s = 2;
			t = 3;
			break;
		case '3':
			s = 3;
			t = 3;
			break;
		case '4':
			s = 4;
			t = 3;
			break;
		case '5':
			s = 5;
			t = 3;
			break;
		case '6':
			s = 6;
			t = 3;
			break;
		case '7':
			s = 7;
			t = 3;
			break;
		case '8':
			s = 8;
			t = 3;
			break;
		case '9':
			s = 9;
			t = 3;
			break;
		case '$':
			s = 4;
			t = 2;
			break;
		case '/':
			s = 15;
			t = 2;
			break;
		case '.':
			s = 14;
			t = 2;
			break;
		case ':':
			s = 10;
			t = 3;
			break;
		case 'Ö':
			s = 9;
			t = 9;
			break;
		case '<':
			s = 12;
			t = 3;
			break;
		case '>':
			s = 14;
			t = 3;
			break;
		case '-':
			s = 13;
			t = 2;
			break;
		case '_':
			s = 15;
			t = 5;
			break;
	
		default:
			System.err.println("Letter \"" + letter + "\" unsupported, please add to Switch case block!");
			break;
		}
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(s * COORD, t * COORD);
		glVertex3d(x, y + letterHeight, 0);
		glTexCoord2d((s + 1) * COORD, t * COORD);
		glVertex3d(x + letterWidth, y + letterHeight, 0);
		glTexCoord2d((s + 1) * COORD, (t + 1) * COORD);
		glVertex3d(x + letterWidth, y, 0);
		glTexCoord2d(s * COORD, (t + 1) * COORD);
		glVertex3d(x, y, 0);
		
		glEnd();
		
	}
	
}
