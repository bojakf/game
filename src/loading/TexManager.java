package loading;

import java.util.HashMap;

public class TexManager {
	
	private static boolean init = false;
	private static HashMap<String, Texture> textures;
	
	public static void init() {
		
		if(init) {
			new Exception("Already initialized!").printStackTrace();
			return;
		}
		
		textures = new HashMap<String, Texture>();
		
	}
	
	public static void loadTex(String name, String location) {
		
		if(textures.containsKey(name)) {
			new Exception("Texture Name already used").printStackTrace();
			return;
		}
		textures.put(name, TextureLoader.load(location));
	
	}
	
	public static void bindTex(String name) {
		textures.get(name).bind();
	}
	
	public static void delTex(String name) {
		textures.get(name).delete();
		textures.remove(name);
	}
	
	public static Texture getTex(String name) {
		return textures.get(name);
	}
	
	public static int getTexID(String name) {
		return textures.get(name).getID();
	}
	
	public static int getTexWidth(String name) {
		return textures.get(name).getWidth();
	}
	
	public static int getTexHeight(String name) {
		return textures.get(name).getHeight();
	}
	
	public static void destroy() {
		
		for(Texture t : textures.values()) {
			t.delete();
		}
		
		textures.clear();
		
		init = false;
		
	}
	
}
