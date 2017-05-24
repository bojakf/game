package loading;

import java.util.HashMap;

/**
 * 
 * Manages loaded textures
 * 
 * TODO add method to remove animated textures and unload them
 * 
 * @author jafi2
 *
 */
public class TexManager {
	
	/**
	 * saves all loaded textures with their name
	 */
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	/**
	 * saves all loaded animation with their name
	 */
	private static HashMap<String, AnimatedTexture> animations = new HashMap<>();
	
	/**
	 * This class contains only static methods
	 */
	private TexManager() {}
	
	/**
	 * Loads a texture<br>
	 * <b>Warning:</b> does not do anything if texture name is already in use
	 * @param name the name of the texture
	 * @param location the location of the texture
	 */
	public static void loadTex(String name, String location) {
		
		if(textures.containsKey(name)) {
			throw new RuntimeException("Texture Name already used:" + name);
		}
		textures.put(name, TextureLoader.load(location));
	
	}
	
	/**
	 * Load a new Animation
	 * @param name the name of the animation
	 * @param location the folder containing the animation. <br> The textures must have the following file structure: [location]\[number of image].png
	 * @param fps the number of frames per second the animation has
	 */
	public static void loadAnimation(String name, String location, int fps) {
		
		if(animations.containsKey(name)) {
			throw new RuntimeException("animation Name already used: " + name);
		}
		animations.put(name, new AnimatedTexture(location, fps));
		
	}
	
	/**
	 * Returns a cloned object of the animation
	 * @param name name of the animation
	 * @return the cloned Animation
	 */
	public static AnimatedTexture getAnimation(String name) {
		if(!animations.containsKey(name))
			throw new RuntimeException("No animation with name: " + name);
		return animations.get(name).clone();
	}
	
	/**
	 * Binds the Texture for use 
	 * @param name the name of the texture
	 */
	public static void bindTex(String name) {
		textures.get(name).bind();
	}
	
	/**
	 * Removes the texture from the texture manager and unloads it
	 * @param name the name of the texture
	 */
	public static void delTex(String name) {
		textures.get(name).delete();
		textures.remove(name);
	}
	
	/**
	 * Get the texture Object for a texture name
	 * @param name the name of the texture
	 * @return the texture object
	 */
	public static Texture getTex(String name) {
		return textures.get(name);
	}
	
	/**
	 * Get the openGL texture id of the texture
	 * @param name the name of the texture
	 * @return the id of the texture
	 */
	public static int getTexID(String name) {
		return textures.get(name).getID();
	}
	
	/**
	 * Get the width of a texture
	 * @param name the name of the texture
	 * @return the width of the texture in pixels
	 */
	public static int getTexWidth(String name) {
		return textures.get(name).getWidth();
	}
	
	/**
	 * Get the height of a texture
	 * @param name the name of the texture
	 * @return the height of the texture in pixels
	 */
	public static int getTexHeight(String name) {
		return textures.get(name).getHeight();
	}
	
	/**
	 * Unloads all textures stored in the texture manager
	 */
	public static void destroy() {
		
		for(Texture t : textures.values()) {
			t.delete();
		}
		
		textures.clear();
		
	}
	
}
