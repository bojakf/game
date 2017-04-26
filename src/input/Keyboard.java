package input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallback.SAM;

import main.Game;

public class Keyboard {

//	private static ArrayList<KeyListener> keyListeners = new ArrayList<KeyListener>();
//	private static ArrayList<CharListener> charListeners = new ArrayList<CharListener>();
	
	private static HashMap<Integer, ArrayList<KeyListener>> keyListeners = new HashMap<>();
	private static HashMap<Integer, ArrayList<CharListener>> charListeners = new HashMap<>();
	
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
				
				ArrayList<KeyListener> kls = keyListeners.get(Game.net.playerID);
				if(kls == null) return;
				
				if(action == GLFW_PRESS) {
					
					for(KeyListener kl : kls) {
						kl.onKeyDown(key, mods);
					}
					
				} else if(action == GLFW_REPEAT) {
					
					for(KeyListener kl : kls) {
						kl.onKeyRepeat(key, mods);
					}
					
				} else if(action == GLFW_RELEASE) {
					
					for(KeyListener kl : kls) {
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
				
				ArrayList<CharListener> cls = charListeners.get(Game.net.playerID);
				if(cls == null) return;
				for(CharListener cl : cls) {
					cl.onChar(input[0]);
				}
				
			}
		}));
		
	}
	
	public static boolean isKeyDown(int key, int playerID) {
		if(playerID == Game.net.playerID) {
			return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
		} else {
			HashMap<Integer, Integer> keyStates = Game.net.getKeyStates(playerID);
			if(keyStates == null) return false;
			return keyStates.get(key) != null && keyStates.get(key) == GLFW.GLFW_PRESS;
		}
	}
	
	public static void destroy() {
		
		init = false;
		window = -1;
		
		keyListeners.clear();
		charListeners.clear();
		
		keyCallback.release();
		charCallback.release();
		
	}
	
	public static void runOnKeyDown(int keycode, int modifiers, int playerID) {
		ArrayList<KeyListener> a = keyListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onKeyDown(keycode, modifiers);
		}
	}
	
	public static void runOnKeyUp(int keycode, int modifiers, int playerID) {
		ArrayList<KeyListener> a = keyListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onKeyUp(keycode, modifiers);
		}
	}
	
	public static void runOnKeyRepeat(int keycode, int modifiers, int playerID) {
		ArrayList<KeyListener> a = keyListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onKeyRepeat(keycode, modifiers);
		}
	}
	
	public static void runOnChar(char c, int playerID) {
		ArrayList<CharListener> a = charListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onChar(c);
		}
	}
	
	public static void addKeyListener(KeyListener kl, int playerID) {
		if(keyListeners.containsKey(playerID)) {
			keyListeners.get(playerID).add(kl);
		} else {
			ArrayList<KeyListener> a;
			keyListeners.put(playerID, a = new ArrayList<KeyListener>());
			a.add(kl);
		}
	}
	
	public static void addCharListener(CharListener cl, int playerID) {
		if(charListeners.containsKey(playerID)) {
			charListeners.get(playerID).add(cl);
		} else {
			ArrayList<CharListener> a;
			charListeners.put(playerID, a = new ArrayList<CharListener>());
			a.add(cl);
		}
	}
	
	public static boolean removeKeyListener(KeyListener kl, int playerID) {
		if(!keyListeners.containsKey(playerID)) return false;
		return keyListeners.get(playerID).remove(kl);
	}
	
	public static boolean removeCharListener(CharListener cl, int playerID) {
		if(!charListeners.containsKey(playerID)) return false;
		return charListeners.get(playerID).remove(cl);
	}
	
	public static abstract class KeyListener {
		public abstract void onKeyDown(int keycode, int modifiers);
		public abstract void onKeyRepeat(int keycode, int modifiers);
		public abstract void onKeyUp(int keycode, int modifiers);
	}
	
	public static abstract class CharListener {
		public abstract void onChar(char input);
	}
	
}
