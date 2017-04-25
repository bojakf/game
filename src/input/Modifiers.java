package input;

import org.lwjgl.glfw.GLFW;

public class Modifiers {
	
	public static final int SHIFT = GLFW.GLFW_MOD_SHIFT;
	public static final int CONTROL = GLFW.GLFW_MOD_CONTROL;
	public static final int ALT = GLFW.GLFW_MOD_ALT;
	public static final int SUPER = GLFW.GLFW_MOD_SUPER;
	
	public boolean isShift(int mods) {
		return (mods & SHIFT) != 0;
	}
	
	public boolean isControl(int mods) {
		return (mods & CONTROL) != 0;
	}
	
	public boolean isAlt(int mods) {
		return (mods & ALT) != 0;
	}
	
	public boolean isSuper(int mods) {
		return (mods & SUPER) != 0;
	}
	
}
