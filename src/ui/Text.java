package ui;

import org.lwjgl.opengl.GL11;

import physics.Vector;
import rendering.StringDrawer;

/**
 * 
 * This class represents a text in the ui
 * 
 * @author jafi2
 *
 */
public class Text extends UiObject {

	/**
	 * The text
	 */
	public String text;
	/**
	 * The position of the text
	 */
	public Vector pos;
	
	/**
	 * Create a new Text in the Ui
	 * @param ui the ui to draw in
	 */
	public Text(Ui ui) {
		super(ui);
	}

	@Override
	protected void update(double deltaTime) {
		
	}

	@Override
	protected void render() {
		GL11.glColor4d(1, 1, 1, 1);
		StringDrawer.drawString(text, (float)pos.x, (float)pos.y);
	}
	
	@Override
	protected void onDestroy() {	
	}

}
