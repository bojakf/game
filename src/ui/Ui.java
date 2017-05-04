package ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import input.Mouse;
import input.Mouse.MouseListener;
import main.Main;

/**
 * 
 * This class represents a ui which can be rendered
 * 
 * @author jafi2
 *
 */
public class Ui {

	/*
	 * Dispatch input from here to ui objects in order to only create one listener for all objects 
	 */
	
	/**
	 * Listener for mouse button states
	 */
	private static MouseListener mouseButtonListener;
	/**
	 * Contains mouse button states for local player
	 */
	private static HashMap<Integer, Boolean> mouseButtons;
	
	/**
	 * All uiObjects of this ui are stored in here
	 */
	private ArrayList<UiObject> uiObjects;
	
	/**
	 * Create a new ui
	 */
	public Ui() {
		
		/*
		 * Create the mouse button listener whit the first ui
		 */
		if(mouseButtonListener == null) {
			mouseButtons = new HashMap<>();
			mouseButtonListener = new MouseListener() {
				
				@Override
				public void onScroll(double delta) {}
				
				@Override
				public void onRelease(int button, int modifiers) {
					mouseButtons.put(button, false);
				}
				
				@Override
				public void onPress(int button, int modifiers) {
					mouseButtons.put(button, true);
				}
			};
			Mouse.addMouseButtonListener(mouseButtonListener, Mouse.LOCAL);
		}
		
		uiObjects = new ArrayList<>();
		
	}
	
	/**
	 * Update the ui
	 * @param deltaTime time since the last frame
	 */
	public void update(double deltaTime) {
		for(int i = 0; i < uiObjects.size(); i++) {
			uiObjects.get(i).update(deltaTime);
		}
	}
	
	/**
	 * Render the ui
	 */
	public void render() {
		GL11.glTranslated(0, 0, -Main.zNEAR);
		for(int i = 0; i < uiObjects.size(); i++) {
			uiObjects.get(i).render();
		}
		GL11.glTranslated(0, 0, Main.zNEAR);
	}
	
	/**
	 * Is the Mouse button down
	 * TODO do this in Mouse class
	 * @param button the number of the mouse button
	 * @return is the mouse button down
	 */
	public static boolean isMouseButtonDown(int button) {
		Boolean b = mouseButtons.get(button);
		return b == null ? false : b;
	}
	
	/**
	 * Adds a object to the ui
	 * @param obj the object
	 */
	protected void registerObject(UiObject obj) {
		uiObjects.add(obj);
	}
	
	/**
	 * Destroys the ui
	 * calls onDestroy on all ui Objects
	 */
	public void destroy() {
		for(int i = 0; i < uiObjects.size(); i++) {
			uiObjects.get(i).onDestroy();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}
	
}
