package input;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback.SAM;

import main.Game;
import main.Main;
import physics.Vector;

/**
 * 
 * This class is used to receive input from the mouse.
 * The appearance of the mouse can be changed too.
 * 
 * @author jafi2
 *
 */
public class Mouse {

	/*
	 * TODO fix setCursorPos
	 * TODO fix setCursorMode
	 */
	
	/**
	 * this class contains only static methods
	 */
	private Mouse() {}
	
	/**
	 * has the class been initialized?
	 */
	private static boolean init = false;
	/**
	 * id of the current openGL window
	 */
	private static long window = -1;
	
	/**
	 * Contains all mouse motion listeners
	 */
	private static HashMap<Integer, ArrayList<MouseMotionListener>> mouseMotionListeners = new HashMap<>();
	/**
	 * Contains all mouse button listeners
	 */
	private static HashMap<Integer, ArrayList<MouseListener>> mouseButtonListeners = new HashMap<>();
	
	/**
	 * Callback for the current position of the mouse
	 */
	private static GLFWCursorPosCallback cursorPosCallback;
	/**
	 * Callback for mouse entering and exiting the window
	 */
	private static GLFWCursorEnterCallback cursorEnterCallback;
	/**
	 * Callback for the mouse buttons
	 */
	private static GLFWMouseButtonCallback mouseButtonCallback;
	/**
	 * Callback for mouse scrolling
	 */
	private static GLFWScrollCallback scrollCallback;
	
	/**
	 * Last reported position of the mouse. Used to determine the delta between two inputs
	 */
	private static double lx = -1, ly = -1;
	
	/**
	 * Initializes the Mouse input<br>
	 * this class should not be used without calling this method first
	 * @param window the id of the openGL window
	 */
	public static void init(long window) {
		
		if(init) {
			new Exception("Mouse already inited!").printStackTrace();
			return;
		}
		
		Mouse.window = window;
		
		mouseMotionListeners.clear();
		mouseButtonListeners.clear();
		
		
		glfwSetCursorPosCallback(window, cursorPosCallback = GLFWCursorPosCallback.create(new SAM() {
			
			@Override
			public void invoke(long window, double x, double y) {
				
				ArrayList<MouseMotionListener> mmls = mouseMotionListeners.get(Game.net.playerID);
				if(mmls == null) return;
				
				for (MouseMotionListener mm : mmls) {
					mm.onMove(lx == -1 ? 0 : x-lx, ly == -1 ? 0 : y-ly);
					lx = x;
					ly = y;
				}
				
			}
		}));
		
		glfwSetCursorEnterCallback(window, cursorEnterCallback = GLFWCursorEnterCallback.create(new GLFWCursorEnterCallback.SAM() {
			
			@Override
			public void invoke(long window, int entered) {
				
				ArrayList<MouseMotionListener> mmls = mouseMotionListeners.get(Game.net.playerID);
				if(mmls == null) return;
				
				if(entered == 0) {
					
					for(MouseMotionListener mm : mmls) {
						mm.onExit();
						lx = -1;
						ly = -1;
					}
					
				} else {
					
					for(MouseMotionListener mm : mmls) {
						mm.onEnter();
					}
					
				}
				
			}
		}));
		
		glfwSetMouseButtonCallback(window, mouseButtonCallback = GLFWMouseButtonCallback.create(new GLFWMouseButtonCallback.SAM() {
			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				
				ArrayList<MouseListener> mbls = mouseButtonListeners.get(Game.net.playerID);
				if(mbls == null) return;
				
				if(action == GLFW_PRESS) {
					
					for(MouseListener mb : mbls) {
						mb.onPress(button, mods);
					}
					
				} else if(action == GLFW_RELEASE) {
					
					for(MouseListener mb : mbls) {
						mb.onRelease(button, mods);
					}
					
				}
				
			}
		}));
		
		
		glfwSetScrollCallback(window, scrollCallback = GLFWScrollCallback.create(new GLFWScrollCallback.SAM() {
			
			@Override
			public void invoke(long window, double xMove, double yMove) {
				
				/*
				 * xMove not available at standard mouses
				 */
				
				ArrayList<MouseListener> mbls = mouseButtonListeners.get(Game.net.playerID);
				if(mbls == null) return;
				
				for(MouseListener mb : mbls) {
					mb.onScroll(yMove);
				}
				
			}
		}));
		
	}
	
	/**
	 * Clears up the mouse listeners and releases the mouse callbacks
	 */
	public static void destroy() {
		
		init = false;
		window = -1;
		
		mouseMotionListeners.clear();
		mouseButtonListeners.clear();
		
		cursorPosCallback.release();
		cursorEnterCallback.release();
		mouseButtonCallback.release();
		scrollCallback.release();
		
	}
	
	/**
	 * Returns the x position of the Mouse relative to the window for the given player
	 * @param playerID the id of the player who's input should by used
	 * @return the x position of the Mouse relative to the window
	 */
	public static double x(int playerID) {
		
		if(playerID == Game.net.playerID) {
			DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1), yPos = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xPos, yPos);
			return xPos.get(0);
		} else {
			Vector v = Game.net.getMousePosition(playerID);
			if(v == null) return 0;
			return v.x;
		}
		
	}
	
	/**
	 * Returns the y position of the Mouse relative to the window for the given player
	 * @param playerID the id of the player who's input should by used
	 * @return the y position of the Mouse relative to the window
	 */
	public static double y(int playerID) {
		
		if(playerID == Game.net.playerID) {
			DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1), yPos = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xPos, yPos);
			return Main.windowHeight - yPos.get(0);
		} else {
			Vector v = Game.net.getMousePosition(playerID);
			if(v == null) return 0;
			return v.y;
		}
		
	}
	
	/**
	 * Returns the xy position of the Mouse relative to the window for the given player
	 * @param playerID the id of the player who's input should by used
	 * @return the xy position of the Mouse relative to the window
	 */
	public static Vector xy(int playerID) {
		
		if(playerID == Game.net.playerID) {
			DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1), yPos = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xPos, yPos);
			return new Vector(xPos.get(0), Main.windowHeight - yPos.get(0));
		} else {
			Vector v = Game.net.getMousePosition(playerID);
			if(v == null) return new Vector();
			return v.clone();
		}
		
	}
	
	/**
	 * Sets the position of the mouse for the given player
	 * @param x the new x position of the mouse
	 * @param y the new y position of the mouse
	 * @param playerID the id of the player who's mouse should be moved
	 */
	@Deprecated
	public static void setCursorPos(double x, double y, int playerID) {
		glfwSetCursorPos(window, x, y);
	}
	
	/**
	 * Sets the visibility mode of the cursor (disabled, hidden, normal)
	 * @param cm the new mode of the cursor
	 * @param playerID the id of the player who's mouse should be modified
	 */
	@Deprecated
	public static void setCursorMode(CursorMode cm, int playerID) {
		
		if(cm == CursorMode.disabled) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		} else if(cm == CursorMode.hidden) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		} else if(cm == CursorMode.normal) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		
	}
	
	/**
	 * 
	 * Available modes for the cursor
	 * <ul>
	 * <li>disabled - the cursor is invisible and grabbed inside the window providing virtual and unlimited cursor movement</li>
	 * <li>hidden - the cursor is invisible as long as the cursor is above the window</li>
	 * <li>normal - the default cursor behavior</li>
	 * </ul>
	 * 
	 * @author jafi2
	 *
	 */
	public enum CursorMode {
		disabled, hidden, normal
	}
	
	/**
	 * Runs the onPress event for the given player
	 * @param button the mouse button pressed
	 * @param modifiers the modifier keys used
	 * @param playerID the id of the player
	 */
	public static void runOnPress(int button, int modifiers, int playerID) {
		ArrayList<MouseListener> a = mouseButtonListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onPress(button, modifiers);
		}
	}
	
	/**
	 * Runs the onRelease event for the given player
	 * @param button the mouse button pressed
	 * @param modifiers the modifier keys used
	 * @param playerID the id of the player
	 */
	public static void runOnRelease(int button, int modifiers, int playerID) {
		ArrayList<MouseListener> a = mouseButtonListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onRelease(button, modifiers);
		}
	}
	
	/**
	 * Runs the onScroll event for the given player
	 * @param delta the number of times scrolled
	 * @param playerID the id of the player
	 */
	public static void runOnScroll(double delta, int playerID) {
		ArrayList<MouseListener> a = mouseButtonListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onScroll(delta);
		}
	}
	
	/**
	 * Runs the onMove event for the given player
	 * @param dx the number of pixels moved in the x direction
	 * @param dy the number of pixels moved in the y direction
	 * @param playerID the id of the player
	 */
	public static void runOnMove(double dx, double dy, int playerID) {
		ArrayList<MouseMotionListener> a = mouseMotionListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onMove(dx, dy);
		}
	}
	
	/**
	 * Runs the onEnter event for the given player
	 * @param playerID the id of the player
	 */
	public static void runOnEnter(int playerID) {
		ArrayList<MouseMotionListener> a = mouseMotionListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onEnter();
		}
	}
	
	/**
	 * Runs the onExit event for the given player
	 * @param playerID the id of the player
	 */
	public static void runOnExit(int playerID) {
		ArrayList<MouseMotionListener> a = mouseMotionListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onExit();
		}
	}
	
	/**
	 * Adds a mouse motion listener for the given player
	 * @param mm the mouse motion listener
	 * @param playerID the id of the player
	 */
	public static void addMouseMotionListener(MouseMotionListener mm, int playerID) {
		if(mouseMotionListeners.containsKey(playerID)) {
			mouseMotionListeners.get(playerID).add(mm);
		} else {
			ArrayList<MouseMotionListener> a;
			mouseMotionListeners.put(playerID, a = new ArrayList<MouseMotionListener>());
			a.add(mm);
		}
	}
	
	/**
	 * Adds a mouse button listener for the given player
	 * @param mb the mouse button listener
	 * @param playerID the id of the player
	 */
	public static void addMouseButtonListener(MouseListener mb, int playerID) {
		if(mouseButtonListeners.containsKey(playerID)) {
			mouseButtonListeners.get(playerID).add(mb);
		} else {
			ArrayList<MouseListener> a;
			mouseButtonListeners.put(playerID, a = new ArrayList<MouseListener>());
			a.add(mb);
		}
	}
	
	/**
	 * Removes the given mouse motion listener for the given player
	 * @param mm the mouse motion listener
	 * @param playerID the id of the player
	 * @return did the listener get removed<br>Also returns false if the listener was not found
	 */
	public static boolean removeMouseMotionListener(MouseMotionListener mm, int playerID) {
		if(!mouseMotionListeners.containsKey(playerID)) return false;
		return mouseMotionListeners.get(playerID).remove(mm);
	}
	
	/**
	 * Removes the given mouse button listener for the given player
	 * @param mb the mouse button listener
	 * @param playerID the id of the player
	 * @return did the listener get removed<br>Also returns false if the listener was not found
	 */
	public static boolean removeMouseButtonListener(MouseListener mb, int playerID) {
		if(!mouseButtonListeners.containsKey(playerID)) return false;
		return mouseButtonListeners.get(playerID).remove(mb);
	}
	
	/**
	 * 
	 * Class for listening to mouse button and scroll input
	 * 
	 * @author jafi2
	 *
	 */
	public static abstract class MouseListener {
		/**
		 * Called if a mouse button is pressed down
		 * @param button the number of the button
		 * @param modifiers the modifier keys pressed
		 */
		public abstract void onPress(int button, int modifiers);
		/**
		 * Called if a mouse button is released
		 * @param button the number of the button
		 * @param modifiers the modifier keys pressed
		 */
		public abstract void onRelease(int button, int modifiers);
		/**
		 * Called if the mouse wheel is moved
		 * @param delta the number of times scrolled
		 */
		public abstract void onScroll(double delta);
	}
	
	/**
	 * 
	 * Class for listening to mouse movement
	 * 
	 * @author jafi2
	 *
	 */
	public static abstract class MouseMotionListener {
		/**
		 * Called if the mouse has moved
		 * @param dx the number of pixels moved in the x direction
		 * @param dy the number of pixels moved in the y direction
		 */
		public abstract void onMove(double dx, double dy);
		/**
		 * Called if the mouse enters the window
		 */
		public abstract void onEnter();
		/**
		 * Called if the mouse leaves the window
		 */
		public abstract void onExit();
	}
	
}
