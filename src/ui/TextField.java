package ui;

import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.*;

import input.Keyboard;
import input.Keyboard.CharListener;
import input.Keyboard.KeyListener;
import input.Mouse;
import physics.Vector;
import rendering.StringDrawer;

/**
 * 
 * This class represents a text field in the ui.
 * 
 * @author jafi2
 *
 */
public abstract class TextField extends UiObject {

	/*
	 * TODO add ability to move cursor with mouse
	 */
	
	/**
	 * The padding of the area writing into
	 */
	private static final double PADDING = 3;
	
	/**
	 * Is the text Field currently selected
	 */
	private boolean selected = false;
	/**
	 * The time the text Field is selected
	 * (for the blinking cursor)
	 */
	private double selectedTime = 0d;
	/**
	 * The position of the cursor
	 */
	private int cursorPos = 0;
	
	/**
	 * Listener for keyboard
	 */
	private CharListener charListener;
	
	/**
	 * Listener for keyboard
	 */
	private KeyListener keyListener;
	
	/**
	 * The text currently typed into the text field
	 */
	public StringBuilder text;
	
	/**
	 * The position of the text field
	 */
	public Vector pos = new Vector();
	/**
	 * The size of the text field
	 */
	public Vector size = new Vector();
	
	/**
	 * Create a new text field
	 * @param ui the ui to render in
	 */
	public TextField(Ui ui) {
		super(ui);
		
		text = new StringBuilder();
		Keyboard.addCharListener(charListener = new Keyboard.CharListener() {
			
			@Override
			public void onChar(char input) {
				if(selected && checkChar(input)) {
					text.insert(cursorPos, input);
					cursorPos++;
				}
			}
		}, Keyboard.LOCAL);
		Keyboard.addKeyListener(keyListener = new Keyboard.KeyListener() {
			
			@Override
			public void onKeyUp(int keycode, int modifiers) {
			}
			
			@Override
			public void onKeyRepeat(int keycode, int modifiers) {
				if(selected && keycode == GLFW.GLFW_KEY_BACKSPACE && text.length()>0) {
					text.deleteCharAt(cursorPos-1);
					cursorPos--;
				} else if(selected && keycode == GLFW.GLFW_KEY_LEFT) {
					cursorPos--;
					cursorPos = Math.max(cursorPos, 0);
				} else if(selected && keycode == GLFW.GLFW_KEY_RIGHT) {
					cursorPos++;
					cursorPos = Math.min(cursorPos, text.length());
				}
			}
			
			@Override
			public void onKeyDown(int keycode, int modifiers) {
				if(selected && keycode == GLFW.GLFW_KEY_BACKSPACE && cursorPos>0) {
					text.deleteCharAt(cursorPos-1);
					cursorPos--;
				} else if(selected && keycode == GLFW.GLFW_KEY_LEFT) {
					cursorPos--;
					cursorPos = Math.max(cursorPos, 0);
				} else if(selected && keycode == GLFW.GLFW_KEY_RIGHT) {
					cursorPos++;
					cursorPos = Math.min(cursorPos, text.length());
				}
			}
		}, Keyboard.LOCAL);
		
	}

	@Override
	protected void update(double deltaTime) {
		
		Vector m = Mouse.xy(Mouse.LOCAL);
		
		if(m.x >= pos.x && m.x <= pos.x+size.x
				&& m.y >= pos.y && m.y <= pos.y+size.y
				&& Ui.isMouseButtonDown(0)) {
			selected = true;
		} else if(Ui.isMouseButtonDown(0)) {
			selected = false;
			selectedTime = 0d;
		}
		
		selectedTime += deltaTime;
		
	}

	@Override
	protected void render() {
		
		/*
		 * TODO remove this if checkChar is working with cursor (send new text with method call
		 */
		cursorPos = text.length();
		
		glColor4d(1, 1, 1, 1);
		
		if(selected && (selectedTime-(int)selectedTime)<0.5d) {
			StringDrawer.drawString("_", cursorPos*StringDrawer.letterWidth + pos.x + 10, pos.y + size.y/2 - StringDrawer.letterHeight/2d);
		}
		StringDrawer.drawString(text.toString(), pos.x + 10, pos.y + size.y/2 - StringDrawer.letterHeight/2d);
		glDisable(GL_TEXTURE_2D);
		
		glBegin(GL_QUADS);
		
		glVertex3d(pos.x, pos.y + size.y, 0.01);
		glVertex3d(pos.x + size.x, pos.y + size.y, 0.01);
		glVertex3d(pos.x + size.x, pos.y, 0.01);
		glVertex3d(pos.x, pos.y, 0.01);
		
		glColor3d(0, 0, 0);
		glVertex2d(pos.x+PADDING, pos.y + size.y-PADDING);
		glColor3d(0.1, 0.1, 0.1);
		glVertex2d(pos.x + size.x-PADDING, pos.y + size.y-PADDING);
		glVertex2d(pos.x + size.x-PADDING, pos.y+PADDING);
		glColor3d(0, 0, 0);
		glVertex2d(pos.x+PADDING, pos.y+PADDING);
		
		glEnd();
		
		glEnable(GL_TEXTURE_2D);
		
	}
	
	@Override
	protected void onDestroy() {
		Keyboard.removeCharListener(charListener, Mouse.LOCAL);
		Keyboard.removeKeyListener(keyListener, Mouse.LOCAL);
	}
	
	/**
	 * Use this for formatted input. Always return true for no formatted input
	 * @param typed the typed char
	 * @return can the char be typed?
	 */
	protected abstract boolean checkChar(char typed);
	
}
