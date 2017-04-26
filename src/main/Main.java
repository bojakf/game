package main;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glFrustum;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLCapabilities;

import input.Keyboard;
import input.Mouse;
import loading.TexManager;

public class Main {

	private static long window;
	
	public static final String name = "Game";
	public static final int windowWidth = 1088, windowHeight = 612;
	public static final double zNEAR = 10;
	public static final double zFAR = -10;
	
	public static boolean isClosing = false;
	
	@SuppressWarnings("unused")
	private static GLCapabilities glCapabilities;
	private Game game;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		
		initWindow();
		initGL();
		Mouse.init(window);
		Keyboard.init(window);
		TexManager.init();
		
		mainLoop();
		
		isClosing = true;
		TexManager.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		destroy();
		
		//Ensure that all threads are closed
		System.exit(0);
		
	}
	
	private void initWindow() {
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if(glfwInit() != 1) {
			new Exception("Error while glfwInit").printStackTrace();
		}
		

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		
		/*
		 * Check if window should be in fullscreen mode
		 */
		
		//if(isFullscreen()) {
		//	window = glfwCreateWindow(data.getWidth(), data.getHeight(), data.getWindowName(), glfwGetPrimaryMonitor(), 0);
		//} else {
		window = glfwCreateWindow(windowWidth, windowHeight, name, 0, 0);
		//}
		
		/*
		 * Check if window is created
		 */
		
		if(window <= 0) {
			new Exception("Error creating window (window ID:" + window + ")").printStackTrace();
			System.exit(-1);
		}
		
		//Center window
		//if(!isFullscreen()) {
		//	glfwSetWindowPos(window, data.getWindowPosX(), data.getWindowPoxY());
		//}
		
		glfwMakeContextCurrent(window);
		
		/*
		 * Enable vSync
		 */
		
		//if(isvSync()) {
			glfwSwapInterval(1);
		//}
		
		glfwShowWindow(window);
		
	}
	
	private void initGL() {
		
		// This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        glCapabilities = GL.createCapabilities(); 
        
		glClearColor(0, 0, 0, 1);
		
//		if(data.isUseDepth()) {
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
//		}
		glEnable(GL_PROJECTION);
		glLoadIdentity();
		
		glMatrixMode(GL_MODELVIEW);
		
		/*
		 * Enable face culling
		 */
		
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		
		
		glEnable(GL_TEXTURE_BIT);
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glShadeModel(GL_SMOOTH);
		glEnable(GL15.GL_ARRAY_BUFFER_BINDING);
		
//		glPerspective(FOV, (double)windowWidth/(double)windowHeight, zNEAR, zFAR);
		
		glClearColor(0, 0, 0f, 0);
		glOrtho(0, windowWidth, 0, windowHeight, zNEAR, zFAR);
		
	}
	
	public static void glPerspective (double fov, double aspect, double zNear, double zFar) {
		
		double fH = Math.tan(fov/360*Math.PI) * zNear;
		double fW = fH * aspect;
		glFrustum(-fW, fW, -fH, fH, zNear, zFar);
		
	}
	
	private void mainLoop() {
		
		double lastTime;
		
		lastTime = System.nanoTime() / 1000000000d;
		
		init();
		
		while(glfwWindowShouldClose(window) == 0) {
			
			double deltaTime = System.nanoTime()/1000000000d - lastTime;
			lastTime = System.nanoTime()/1000000000d;
			
			//Calls all callbacks
			glfwPollEvents();
			
			update(deltaTime);
			
			/*
			 * Prepare rendering
			 */
			GL.createCapabilities();
			glPushMatrix();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			render();
			
			glPopMatrix();
			glfwSwapBuffers(window);
			
		}
		
		game.onClose();
		
	}
	
	private void init() {
		game = new Game();		
	}
	
	private void update(double deltaTime) {
		game.update(deltaTime);
	}
	
	private void render() {
		game.render();		
	}
	
	
	public void destroy() {
				
		//glfwReleaseCallbacks(window);
        glfwDestroyWindow(window);
        
	}

}
