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

/**
 * 
 * Contains methods for getting keyboard input
 * 
 * @author jafi2
 *
 */
public class Keyboard {
	
	/**
	 * use this as player id for local input
	 */
	public static final int LOCAL = Integer.MIN_VALUE;
	
	/**
	 * This class contains only static methods
	 */
	private Keyboard() {}
	
	/**
	 * Contains key listeners for all players <br>
	 * key of the hashMap is the playerID
	 */
	private static HashMap<Integer, ArrayList<KeyListener>> keyListeners = new HashMap<>();
	/**
	 * Contains char listeners for all players <br>
	 * key of the hashMap is the playerID
	 */
	private static HashMap<Integer, ArrayList<CharListener>> charListeners = new HashMap<>();
	
	/**
	 * has the class been initialized?
	 */
	private static boolean init = false;
	/**
	 * id of the current opengl window
	 */
	private static long window = -1;
	/**
	 * callback for raw key input
	 */
	private static GLFWKeyCallback keyCallback;
	/**
	 * callback for char input
	 */
	private static GLFWCharModsCallback charCallback;
	
	/**
	 * Initializes the keyboard input<br>
	 * this class should not be used without calling this method first
	 * @param window the id of the openGL window
	 */
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
				
				ArrayList<KeyListener> kls = null;
				if(Game.net != null) kls = keyListeners.get(Game.net.playerID);
				if(kls == null)
					kls = keyListeners.get(LOCAL);
				else {
					ArrayList<KeyListener> a = keyListeners.get(LOCAL);
					if(a != null) kls.addAll(a);
				}
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
				
				ArrayList<CharListener> cls = null;
				if(Game.net != null) cls = charListeners.get(Game.net.playerID);
				if(cls == null)
					cls = charListeners.get(LOCAL);
				else {
					ArrayList<CharListener> a = charListeners.get(LOCAL);
					if(a != null) cls.addAll(a);
				}
				if(cls == null) return;
				
				for(CharListener cl : cls) {
					cl.onChar(input[0]);
				}
				
			}
		}));
		
	}
	
	/**
	 * Is the given key down for the given player<br>
	 * If the playerID equals Game.net.playerID the local input is check otherwise the input is received through network
	 * @param key GLFW.GLFW_KEY_Keyname
	 * @param playerID the id of player who's input should be used<br>use Mouse.LOCAL for local input
	 * @return is the key down?
	 */
	public static boolean isKeyDown(int key, int playerID) {
		if(playerID == LOCAL || playerID == Game.net.playerID) {
			return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
		} else {
			HashMap<Integer, Integer> keyStates = Game.net.getKeyStates(playerID);
			if(keyStates == null) return false;
			return keyStates.get(key) != null && keyStates.get(key) == GLFW.GLFW_PRESS;
		}
	}
	
	/**
	 * Cleans up Keyboard listeners and releases callbacks
	 */
	public static void destroy() {
		
		init = false;
		window = -1;
		
		keyListeners.clear();
		charListeners.clear();
		
		keyCallback.release();
		charCallback.release();
		
	}
	
	/**
	 * Runs the onKeyDown Event for the given player
	 * @param keycode GLFW code of the key
	 * @param modifiers used modification keys (e.g. Crtl)
	 * @param playerID id of the player
	 */
	public static void runOnKeyDown(int keycode, int modifiers, int playerID) {
		ArrayList<KeyListener> a = keyListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onKeyDown(keycode, modifiers);
		}
	}
	
	/**
	 * Runs the onKeyUp Event for the given player
	 * @param keycode GLFW code of the key
	 * @param modifiers used modification keys (e.g. Crtl)
	 * @param playerID id of the player
	 */
	public static void runOnKeyUp(int keycode, int modifiers, int playerID) {
		ArrayList<KeyListener> a = keyListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onKeyUp(keycode, modifiers);
		}
	}
	
	/**
	 * Runs the onKeyRepeat Event for the given player
	 * @param keycode GLFW code of the key
	 * @param modifiers used modification keys (e.g. Crtl)
	 * @param playerID id of the player
	 */
	public static void runOnKeyRepeat(int keycode, int modifiers, int playerID) {
		ArrayList<KeyListener> a = keyListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onKeyRepeat(keycode, modifiers);
		}
	}
	
	/**
	 * Runs the onChar Event for the given player
	 * @param c char typed
	 * @param playerID id of the player
	 */
	public static void runOnChar(char c, int playerID) {
		ArrayList<CharListener> a = charListeners.get(playerID);
		for(int i = 0; a != null && i < a.size(); i++) {
			a.get(i).onChar(c);
		}
	}
	
	/**
	 * Adds a key listener to the keyboard
	 * @param kl the key listener
	 * @param playerID the id of the player who's input should be used<br>use Mouse.LOCAL for local input
	 */
	public static void addKeyListener(KeyListener kl, int playerID) {
		if(keyListeners.containsKey(playerID)) {
			keyListeners.get(playerID).add(kl);
		} else {
			ArrayList<KeyListener> a;
			keyListeners.put(playerID, a = new ArrayList<KeyListener>());
			a.add(kl);
		}
	}
	
	/**
	 * Adds a char listener to the keyboard
	 * @param cl the char listener
	 * @param playerID the id of the player who's input should be used<br>use Mouse.LOCAL for local input
	 */
	public static void addCharListener(CharListener cl, int playerID) {
		if(charListeners.containsKey(playerID)) {
			charListeners.get(playerID).add(cl);
		} else {
			ArrayList<CharListener> a;
			charListeners.put(playerID, a = new ArrayList<CharListener>());
			a.add(cl);
		}
	}
	
	/**
	 * Removes the given key Listener from the keyboard
	 * @param kl the key listener to remove
	 * @param playerID the id of the player who's listener should be removed<br>use Mouse.LOCAL for local input
	 * @return did the listener get removed <br>Also returns false if the key listener was not found
	 */
	public static boolean removeKeyListener(KeyListener kl, int playerID) {
		if(!keyListeners.containsKey(playerID)) return false;
		return keyListeners.get(playerID).remove(kl);
	}
	
	/**
	 * Removes the given char Listener from the keyboard
	 * @param cl the char listener
	 * @param playerID the id of the player who's listener should be removed<br>use Mouse.LOCAL for local input
	 * @return did the listener get removed <br>Also returns false if the key listener was not found
	 */
	public static boolean removeCharListener(CharListener cl, int playerID) {
		if(!charListeners.containsKey(playerID)) return false;
		return charListeners.get(playerID).remove(cl);
	}
	
	/**
	 * 
	 * Class for listening to raw Keyboard input
	 * 
	 * @author jafi2
	 *
	 */
	public static abstract class KeyListener {
		/**
		 * Called if a key is pressed down
		 * @param keycode the GLFW code of the key
		 * @param modifiers the modifier keys used. Use input.Modifiers class to get decode the Information.
		 */
		public abstract void onKeyDown(int keycode, int modifiers);
		/**
		 * Called if a key is being held down for a longer period of time
		 * @param keycode the GLFW code of the key
		 * @param modifiers the modifier keys used. Use input.Modifiers class to get decode the Information.
		 */
		public abstract void onKeyRepeat(int keycode, int modifiers);
		/**
		 * Called if a key is released
		 * @param keycode the GLFW code of the key
		 * @param modifiers the modifier keys used. Use input.Modifiers class to get decode the Information.
		 */
		public abstract void onKeyUp(int keycode, int modifiers);
	}
	
	/**
	 * 
	 * Class for listening to char input<br>
	 * This input is already formated and does not contains any information on which modifier keys were used, but the char is effected
	 * by the modifier keys.
	 * 
	 * @author jafi2
	 *
	 */
	public static abstract class CharListener {
		/**
		 * Called if a char is typed
		 * @param input the character typed
		 */
		public abstract void onChar(char input);
	}
	
}
