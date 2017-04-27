package loading;

import java.util.HashMap;

/**
 * 
 * Manages loaded textures
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
			new Exception("Texture Name already used").printStackTrace();
			return;
		}
		textures.put(name, TextureLoader.load(location));
	
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
