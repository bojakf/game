package loading;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

/**
 * 
 * This class is used to store a openGL Texture
 * 
 * @author jafi2
 *
 */
public class Texture {
	
	/**
	 * The id of the texture
	 */
	private int id;
	/**
	 * The width of the texture
	 */
	private int width;
	/**
	 * the height of the texture
	 */
	private int height;
	
	/**
	 * Uploads a texture to the GPU
	 * @param width the width of the texture
	 * @param height the height of the texture
	 * @param data the pixel information of the texture
	 * @param nearest false: uses bilinear texture sampling, true: does not use texture sampling
	 */
	public Texture(int width, int height, ByteBuffer data, boolean nearest) {
		
		id = glGenTextures();
		this.width = width;
		this.height = height;
		
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		if(nearest) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		}
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
		
	}
	
	/**
	 * Bind the texture to use for rendering
	 */
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	/**
	 * Remove the texture from the GPU
	 */
	protected void delete() {
		glDeleteTextures(id);
	}

	/**
	 * get the width of the texture
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * get the height of the texture
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * get the openGL texture id of the texture
	 * @return the openGL texture id
	 */
	public int getID() {
		return id;
	}
	
}
