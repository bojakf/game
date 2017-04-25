package loading;

import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

class TextureLoader {
	
	public static Texture load(String path) {
		
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		
		ByteBuffer image = stbi_load(path, w, h, comp, 4);
		if(image == null) {
			throw new RuntimeException("Faild to load texture " + path + ":" + System.lineSeparator() + stbi_failure_reason());
		}
		
		return new Texture(w.get(), h.get(), image, true);
		
	}
	
}
