package loading;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

/**
 * 
 * Used to upload a shader from file to GPU
 * <br>
 * To change the used shader use ARBShaderObjects.glUseProgramObjectARB(id of the shader);
 * 
 * @author jafi2
 *
 */
public class ShaderLoader {
	
	/**
	 * this class contains only static methods
	 */
	private ShaderLoader() {}
	
	/**
	 * Uploads a shader to the gpu
	 * and prints shader debug information
	 * @param vertexShaderName the location of the vertex shader
	 * @param fragmentShaderName the location of the fragment shader
	 * @return the id of the shader object
	 */
	public static int loadShader(String vertexShaderName, String fragmentShaderName) {
		
		int program = 0;
		int vertexShader = 0;
		int fragmentShader = 0;
		
		try {
			vertexShader = createShader(vertexShaderName, ARBVertexShader.GL_VERTEX_SHADER_ARB);
			fragmentShader = createShader(fragmentShaderName, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if(vertexShader == 0 || fragmentShader == 0) {
				return 0;
			}
		}
		
		program = ARBShaderObjects.glCreateProgramObjectARB();
		if(program == 0) {
			return 0;
		}
		
		ARBShaderObjects.glAttachObjectARB(program, vertexShader);
		ARBShaderObjects.glAttachObjectARB(program, fragmentShader);
		
		ARBShaderObjects.glLinkProgramARB(program);
		if(ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program));
			return 0;
		}
		
		return program;
	}
	
	/**
	 * Creates a shader of the specified type
	 * @param filename the location of the shader file
	 * @param shaderType the type of the shader
	 * @return the id of the shader
	 * @throws Exception thrown exceptions during shader creation
	 */
	private static int createShader(String filename, int shaderType) throws Exception {
		
		int shader = 0;
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
			
			if(shader == 0) {
				return 0;
			}
			
			ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
			ARBShaderObjects.glCompileShaderARB(shader);
			
			if(ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
				throw new RuntimeException("Error Creating shader: " + getLogInfo(shader));
			}
			
		} catch(Exception e) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			throw e;
		}
		
		System.out.println("Shader Info");
		System.out.println(getLogInfo(shader));
		
		return shader;
		
	}
	
	/**
	 * get the log info from a shader
	 * @param obj the shader id
	 * @return the log info
	 */
	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	/**
	 * Reads a shader file in as string
	 * @param filename the location of the file
	 * @return the text read from the file
	 * @throws Exception exceptions thrown during loading
	 */
	private static String readFileAsString(String filename) throws Exception {
		
		StringBuilder source = new StringBuilder();
		
		FileInputStream in = new FileInputStream(filename);
		
		Exception e = null;
		BufferedReader reader;
		
		try {
			
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			Exception innerExc = null;
			try {
				
				String line;
				while((line = reader.readLine()) != null) {
					source.append(line).append('\n');
				}
				
			}catch(Exception exc) {
				e = exc;
			} finally {
				try {
					reader.close();
				} catch (Exception exc) {
					if(innerExc == null) {
						innerExc = exc;
					} else {
						exc.printStackTrace();
					}
				}
			}
			
			if(innerExc != null) {
				throw innerExc;
			}
			
			
		} catch (Exception ex) {
			e = ex;
		} finally {
			
			try {
				in.close();
			} catch (Exception exc) {
				if(e == null) {
					e = exc;
				} else {
					exc.printStackTrace();
				}
			}
			
			if(e != null) {
				throw e;
			}
			
		}
		return source.toString();
		
	}
	
}
