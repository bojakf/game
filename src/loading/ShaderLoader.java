package loading;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

public class ShaderLoader {
	
	private static int program = 0;
	private static int vertexShader = 0, fragmentShader = 0;
	
	public static int loadShader(String vertexShaderName, String fragmentShaderName) {
		
		program = 0;
		vertexShader = 0;
		fragmentShader = 0;
		
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
	
	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	private static String readFileAsString(String filename) throws Exception {
		
		StringBuilder source = new StringBuilder();
		
		FileInputStream in = new FileInputStream("D:\\workspace\\SpaceshipShooter\\src\\" + filename);
		
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
