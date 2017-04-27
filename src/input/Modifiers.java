package input;

import org.lwjgl.glfw.GLFW;

/**
 * 
 * Class for decoding GLFW modifiers
 * more than one modifier key can be pressed at once
 * 
 * @author jafi2
 *
 */
public class Modifiers {
	
	/**
	 * is shift down
	 * @param mods the modifiers value provided by input listeners
	 * @return is shift down
	 */
	public boolean isShift(int mods) {
		return (mods & GLFW.GLFW_MOD_SHIFT) != 0;
	}
	
	/**
	 * is crtl down
	 * @param mods the modifiers value provided by input listeners
	 * @return is crtl down
	 */
	public boolean isControl(int mods) {
		return (mods & GLFW.GLFW_MOD_CONTROL) != 0;
	}
	
	/**
	 * is alt down
	 * @param mods the modifiers value provided by input listeners
	 * @return is alt down
	 */
	public boolean isAlt(int mods) {
		return (mods & GLFW.GLFW_MOD_ALT) != 0;
	}
	
	/**
	 * is the super key down<br>
	 * <b>Note: </b>e.g. Windows key
	 * @param mods the modifiers value provided by input listeners
	 * @return is the super key down
	 */
	public boolean isSuper(int mods) {
		return (mods & GLFW.GLFW_MOD_SUPER) != 0;
	}
	
}
