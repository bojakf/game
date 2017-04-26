package input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallback.SAM;

public class Keyboard {

	private static ArrayList<KeyListener> keyListeners = new ArrayList<KeyListener>();
	private static ArrayList<CharListener> charListeners = new ArrayList<CharListener>();
	
	private static boolean init = false;
	private static long window = -1;
	private static GLFWKeyCallback keyCallback;
	private static GLFWCharModsCallback charCallback;
	
	public static void init(long window) {
		
		if(init) {
			new Exception("Already inited").printStackTrace();
			return;
		}
		
		Keyboard.window = window;
		
		/*
		 * Clear Listeners
		 */
		
		keyListeners.clear();
		charListeners.clear();
		
		
		glfwSetKeyCallback(window, keyCallback = GLFWKeyCallback.create(new SAM() {
			
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				
				if(action == GLFW_PRESS) {
					
					for(KeyListener kl : keyListeners) {
						kl.onKeyDown(key, mods);
					}
					
				} else if(action == GLFW_REPEAT) {
					
					for(KeyListener kl : keyListeners) {
						kl.onKeyRepeat(key, mods);
					}
					
				} else if(action == GLFW_RELEASE) {
					
					for(KeyListener kl : keyListeners) {
						kl.onKeyUp(key, mods);
					}
					
				}
				
			}
			
		}));
		
		
		/*
		 * 
		 * 
		 * GLFW_MOD_SUPER == Windows Key
		 * 
		 * 
		 */		
		
		glfwSetCharModsCallback(window, charCallback = GLFWCharModsCallback.create(new GLFWCharModsCallback.SAM() {
			
			@Override
			public void invoke(long window, int codePoint, int mods) {
				
				char input[] = Character.toChars(codePoint);
				
				for(CharListener cl : charListeners) {
					cl.onChar(input[0]);
				}
				
			}
		}));
		
	}
	
	@Deprecated
	public static boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
	}
	
	public static void destroy() {
		
		init = false;
		window = -1;
		
		keyListeners.clear();
		charListeners.clear();
		
		keyCallback.release();
		charCallback.release();
		
	}
	
	@Deprecated
	public static void addKeyListener(KeyListener kl) {
		keyListeners.add(kl);
	}
	
	@Deprecated
	public static void addCharListener(CharListener cl) {
		charListeners.add(cl);
	}
	
	@Deprecated
	public static boolean removeKeyListener(KeyListener kl) {
		return keyListeners.remove(kl);
	}
	
	@Deprecated
	public static boolean removeCharListener(CharListener cl) {
		return charListeners.remove(cl);
	}
	
	@Deprecated
	public static abstract class KeyListener {
		public abstract void onKeyDown(int keycode, int modifiers);
		public abstract void onKeyRepeat(int keycode, int modifiers);
		public abstract void onKeyUp(int keycode, int modifiers);
	}
	
	@Deprecated
	public static abstract class CharListener {
		public abstract void onChar(char input);
	}
	
}
