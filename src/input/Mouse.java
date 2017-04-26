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

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback.SAM;

import main.Main;

public class Mouse {

	private static boolean init = false;
	private static long window = -1;
	
	private static ArrayList<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
	private static ArrayList<MouseListener> mouseButtonListeners = new ArrayList<MouseListener>();
	
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
				
				for (MouseMotionListener mm : mouseMotionListeners) {
					mm.onMove(lx == -1 ? 0 : x-lx, ly == -1 ? 0 : y-ly);
					lx = x;
					ly = y;
				}
				
			}
		}));
		
		glfwSetCursorEnterCallback(window, cursorEnterCallback = GLFWCursorEnterCallback.create(new GLFWCursorEnterCallback.SAM() {
			
			@Override
			public void invoke(long window, int entered) {
				
				if(entered == 0) {
					
					for(MouseMotionListener mm : mouseMotionListeners) {
						mm.onExit();
						lx = -1;
						ly = -1;
					}
					
				} else {
					
					for(MouseMotionListener mm : mouseMotionListeners) {
						mm.onEnter();
					}
					
				}
				
			}
		}));
		
		glfwSetMouseButtonCallback(window, mouseButtonCallback = GLFWMouseButtonCallback.create(new GLFWMouseButtonCallback.SAM() {
			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				
				if(action == GLFW_PRESS) {
					
					for(MouseListener mb : mouseButtonListeners) {
						mb.onPress(button, mods);
					}
					
				} else if(action == GLFW_RELEASE) {
					
					for(MouseListener mb : mouseButtonListeners) {
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
				
				for(MouseListener mb : mouseButtonListeners) {
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
	
	@Deprecated
	public static double x() {
		
		DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1), yPos = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, xPos, yPos);
		return xPos.get(0);
		
	}
	
	@Deprecated
	public static double y() {
		
		DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1), yPos = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, xPos, yPos);
		return Main.windowHeight - yPos.get(0);
		
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
	@Deprecated
	public static void addMouseMotionListener(MouseMotionListener mm) {
		mouseMotionListeners.add(mm);
	}
	@Deprecated
	public static void addMouseButtonListener(MouseListener mb) {
		mouseButtonListeners.add(mb);
	}
	@Deprecated
	public static void removeMouseMotionListener(MouseMotionListener mm) {
		mouseMotionListeners.remove(mm);
	}
	@Deprecated
	public static void removeMouseButtonListener(MouseListener mb) {
		mouseButtonListeners.remove(mb);
	}
	
}
