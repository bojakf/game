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
import static org.lwjgl.opengl.GL11.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLCapabilities;

import input.Keyboard;
import input.Mouse;
import loading.MultiOutStream;
import loading.TexManager;

/**
 * 
 * Contains main entry point of the program<br>
 * Also manages OpenGL initialization
 * Contains the main loop
 * 
 * @author jafi2
 *
 */
public class Main {
	
	/**
	 * The openGL id of the current window
	 */
	private static long window;
	
	/**
	 * The name of the window
	 */
	public static final String name = "Game";
	/**
	 * The width of the window
	 */
	public static final int windowWidth = 1088;
	/**
	 * The height of the window
	 */
	public static final int windowHeight = 612;
	/**
	 * zNear for rendering
	 */
	public static final double zNEAR = 10;
	/**
	 * zFar for rendering
	 */
	public static final double zFAR = -10;
	
	/**
	 * true if the window is closing
	 */
	public static boolean isClosing = false;
	
	/**
	 * Is vSync currently active (must not be correct)
	 */
	public static boolean isVSync = true;
	
	/**
	 * This is a reference to glCapabilities
	 * <b>Warning: </b>this reference must not be deleted by the garbage collector before the application is closed. 
	 * It contains necessary information in native code. OpenGL will not work without it.
	 */
	@SuppressWarnings("unused")
	private static GLCapabilities glCapabilities;
	/**
	 * The actual game
	 */
	private Game game;
	
	/**
	 * The main of the Game
	 * @param args the arguments
	 * @throws FileNotFoundException thrown if the errLog.txt or log.txt could not be opened
	 */
	public static void main(String[] args) throws FileNotFoundException {
		MultiOutStream err = new MultiOutStream();
		MultiOutStream out = new MultiOutStream();
		err.out.add(new FileOutputStream("errLog.txt"));
		err.out.add(System.err);
		out.out.add(new FileOutputStream("log.txt"));
		out.out.add(System.out);
		
		System.setErr(new PrintStream(err, true));
		System.setOut(new PrintStream(out, true));
		
		new Main();
	}
	
	/**
	 * The contructor of main everything is called from here
	 */
	public Main() {
		
		initWindow();
		initGL();
		Mouse.init(window);
		Keyboard.init(window);
		
		mainLoop();
		
		isClosing = true;
		TexManager.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		destroy();
		
		//Ensure that all threads are closed
		System.exit(0);
		
	}
	
	/**
	 * Init the OpenGL window
	 */
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
	
	/**
	 * Init OpenGL
	 */
	private void initGL() {
		
		// This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        glCapabilities = GL.createCapabilities(); 
        
		glClearColor(0, 0, 0, 1);
		
//		if(data.isUseDepth()) {
//		glEnable(GL_DEPTH_TEST);
//		glDepthFunc(GL_LESS);
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
		//glEnable(GL_ALPHA_TEST);
		//glAlphaFunc(GL_GREATER, 0.9f);
		
		glShadeModel(GL_SMOOTH);
		glEnable(GL15.GL_ARRAY_BUFFER_BINDING);
		
//		glPerspective(FOV, (double)windowWidth/(double)windowHeight, zNEAR, zFAR);
		
		glClearColor(0, 0.247f, 0.53f, 1);
		glOrtho(0, windowWidth, 0, windowHeight, zNEAR, zFAR);
		
	}
	
	/**
	 * set the transformation matrix for 3d perspective rendering
	 * @param fov Field of View
	 * @param aspect aspect ratio of the screen ((double)windowWidth/windowHeigt)
	 * @param zNear start of the rendered area
	 * @param zFar end of the rendered area
	 */
	public static void glPerspective (double fov, double aspect, double zNear, double zFar) {
		
		double fH = Math.tan(fov/360*Math.PI) * zNear;
		double fW = fH * aspect;
		glFrustum(-fW, fW, -fH, fH, zNear, zFar);
		
	}
	
	/**
	 * Calls all game methods in main Thread.<br>
	 * Also calculates deltaTimes polls Input events and prepares rendering
	 */
	private void mainLoop() {
		
		double lastTime;
		
		lastTime = System.nanoTime() / 1000000000d;
		
		init();
		
		while(glfwWindowShouldClose(window) == 0) {
			
			double deltaTime = System.nanoTime()/1000000000d - lastTime;
			lastTime = System.nanoTime()/1000000000d;
			
			//Calls all callbacks
			glfwPollEvents();
			
			game.update(deltaTime);
			
			/*
			 * Prepare rendering
			 */
			GL.createCapabilities();
			glPushMatrix();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			game.render();
			
			glPopMatrix();
			glfwSwapBuffers(window);
			
		}
		
		game.onClose();
		
	}
	
	/**
	 * Called directly before the main loop starts.
	 * Here the game is being initialized
	 */
	private void init() {
		game = new Game();		
	}
	
	/**
	 * this method closes the window. It should not be called before the game closes
	 */
	public void destroy() {
				
		//glfwReleaseCallbacks(window);
        glfwDestroyWindow(window);
        
	}
	
	/**
	 * Returns the id of the current opengl window
	 * @return the id of the window
	 */
	public long getWindowID() {
		return window;
	}

}
