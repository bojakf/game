package ui;

import static org.lwjgl.opengl.GL11.*;

import input.Mouse;
import loading.TexManager;
import physics.Vector;
import rendering.StringDrawer;

/**
 * 
 * This class represents a button in the ui
 * 
 * @author jafi2
 *
 */
public class Button extends UiObject {
	
	public static final String STD_CLICKED_TEX = "button_clicked";
	public static final String STD_MOUSE_OVER_TEX = "button_mouseOver";
	public static final String STD_NORMAL_TEX = "button_normal";
	
	/**
	 * The position of the button
	 */
	public Vector pos = new Vector();
	/**
	 * The size of the button
	 */
	public Vector size = new Vector();
	/**
	 * the clicked texture
	 */
	public String clickedTex = STD_CLICKED_TEX;
	/**
	 * The normal texture
	 */
	public String normalTex = STD_NORMAL_TEX;
	/**
	 * The mosue over texture
	 */
	public String mouseOverTex = STD_MOUSE_OVER_TEX;
	/**
	 * The text the button displays
	 */
	public String text = "NULL";
	
	/**
	 * Is the mouse over the button
	 */
	private boolean over = false;
	/**
	 * Is this the beginning of a click
	 */
	private boolean firstClicked = false;
	/**
	 * Is the button clicked
	 */
	private boolean clicked = false;
	/**
	 * The click Listener for callback
	 */
	private ClickListener clickListener;
	
	/**
	 * Creates a new button
	 * @param ui the ui to draw in
	 * @param clickListener callback for clicking on button
	 */
	public Button(Ui ui, ClickListener clickListener) {
		super(ui);
		if(clickListener == null) {
			throw new NullPointerException("ClickListener must not be null");
		}
		this.clickListener = clickListener;
	}

	@Override
	protected void update(double deltaTime) {
		
		Vector m = Mouse.xy(Mouse.LOCAL);
		if(m.x >= pos.x && m.y <= pos.x + size.x &&
				m.y >= pos.y && m.y <= pos.y + size.y) {
			over = true;
			if(Ui.isMouseButtonDown(0)) {
				clicked = true;
				if(firstClicked) {
					firstClicked = false;
					clickListener.onClick();
				}
			} else {
				firstClicked = true;
				clicked = false;
			}
		} else {
			firstClicked = true;
			clicked = false;
			over = false;
		}
		
	}

	@Override
	protected void render() {
		
		glColor4d(1, 1, 1, 1);
		
		StringDrawer.drawStringCentered(text, (float)(pos.x + size.x/2), (float)(pos.y + size.y/2));
		
		
		if(clicked) {
			TexManager.bindTex(clickedTex);
		} else if(over) {
			TexManager.bindTex(mouseOverTex);
		} else {
			TexManager.bindTex(normalTex);
		}
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(0,0);
		glVertex2d(pos.x, pos.y + size.y);
		
		glTexCoord2d(1, 0);
		glVertex2d(pos.x + size.x, pos.y + size.y);
		
		glTexCoord2d(1, 1);
		glVertex2d(pos.x + size.x, pos.y);
		
		glTexCoord2d(0, 1);
		glVertex2d(pos.x, pos.y);
		
		glEnd();
		
	}
	
	@Override
	protected void onDestroy() {		
	}
	
	/**
	 * Callback for clicking on the button
	 */
	public static abstract class ClickListener {
		/**
		 * Called when the button is clicked
		 */
		public abstract void onClick();
	}
	
}
