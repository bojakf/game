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

public class Mouse {

	/*
	 * TODO fix setCursorPos
	 * TODO fix setCursorMode
	 */
	
	private static boolean init = false;
	private static long window = -1;
	
	private static HashMap<Integer, ArrayList<MouseMotionListener>> mouseMotionListeners = new HashMap<>();
	private static HashMap<Integer, ArrayList<MouseListener>> mouseButtonListeners = new HashMap<>();
	
	private static GLFWCursorPosCallback cursorPosCallback;
	private static GLFWCursorEnterCallback cursorEnterCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;
	private static GLFWScrollCallback scrollCallback;
	
	private static double lx = -1, ly = -1;
	
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
	
	@Deprecated
	public static void setCursorPos(double x, double y) {
		glfwSetCursorPos(window, x, y);
	}
	
	@Deprecated
	public static void setCursorMode(CursorMode cm) {
		
		if(cm == CursorMode.disabled) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		} else if(cm == CursorMode.hidden) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		} else if(cm == CursorMode.normal) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		
	}
	
	public enum CursorMode {
		disabled, hidden, normal
	}
	
	public static void runOnPress(int button, int modifiers, int playerID) {
		ArrayList<MouseListener> a = mouseButtonListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onPress(button, modifiers);
		}
	}
	
	public static void runOnRelease(int button, int modifiers, int playerID) {
		ArrayList<MouseListener> a = mouseButtonListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onRelease(button, modifiers);
		}
	}
	
	public static void runOnScroll(double delta, int playerID) {
		ArrayList<MouseListener> a = mouseButtonListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onScroll(delta);
		}
	}
	
	public static void runOnMove(double dx, double dy, int playerID) {
		ArrayList<MouseMotionListener> a = mouseMotionListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onMove(dx, dy);
		}
	}
	
	public static void runOnEnter(int playerID) {
		ArrayList<MouseMotionListener> a = mouseMotionListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onEnter();
		}
	}
	
	public static void runOnExit(int playerID) {
		ArrayList<MouseMotionListener> a = mouseMotionListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onExit();
		}
	}
	
	public static void addMouseMotionListener(MouseMotionListener mm, int playerID) {
		if(mouseMotionListeners.containsKey(playerID)) {
			mouseMotionListeners.get(playerID).add(mm);
		} else {
			ArrayList<MouseMotionListener> a;
			mouseMotionListeners.put(playerID, a = new ArrayList<MouseMotionListener>());
			a.add(mm);
		}
	}
	
	public static void addMouseButtonListener(MouseListener mb, int playerID) {
		if(mouseButtonListeners.containsKey(playerID)) {
			mouseButtonListeners.get(playerID).add(mb);
		} else {
			ArrayList<MouseListener> a;
			mouseButtonListeners.put(playerID, a = new ArrayList<MouseListener>());
			a.add(mb);
		}
	}
	
	public static boolean removeMouseMotionListener(MouseMotionListener mm, int playerID) {
		if(!mouseMotionListeners.containsKey(playerID)) return false;
		return mouseMotionListeners.get(playerID).remove(mm);
	}
	
	public static boolean removeMouseButtonListener(MouseListener mb, int playerID) {
		if(!mouseButtonListeners.containsKey(playerID)) return false;
		return mouseButtonListeners.get(playerID).remove(mb);
	}
	
	public static abstract class MouseListener {
		public abstract void onPress(int button, int modifiers);
		public abstract void onRelease(int button, int modifiers);
		public abstract void onScroll(double delta);
	}
	
	public static abstract class MouseMotionListener {
		public abstract void onMove(double dx, double dy);
		public abstract void onEnter();
		public abstract void onExit();
	}
	
}
