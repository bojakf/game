package main;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import debug.Debug;
import gameobject.Component;
import gameobject.Gameobject;
import gameobject.Primitive;
import levels.Level;
import levels.MainMenu;
import loading.TexManager;
import map.CameraController;
import network.Network;

/**
 * 
 * This is the Main Class for the game
 * all actual game Logic starts here
 * 
 * @author jafi2
 *
 */

public class Game {
	
	/*
	 * TODO knockback weapon
	 * TODO grenade knockback
	 */
	/*
	 * TODO system for saving options
	 */
	/*
	 * TODO weapon selection 
	 */
	/*
	 * TODO change network input handling (client has to use server key bindings)
	 */
	/*
	 * TODO add taskbar icon
	 */
	/*
	 * TODO maybe add option to use multicast to send server updates to the clients
	 */
	/*
	 * TODO mipmaping
	 */
	/*
	 * TODO texture packs
	 */
	/*
	 * TODO weapons as components?
	 */
	/*
	 * TODO (networking interpolation for low framerates)
	 */
	
	/**
	 * Number of quads fitting in y-Direction on screen
	 */
	public static final double QUADS_Y = 15d;
	/**
	 * Size of one Quad
	 */
	public static final double QUAD_SIZE = Main.windowHeight/QUADS_Y;
	/**
	 * Number of quads fitting in x-Direction on screen
	 */
	public static final double QUADS_X = Main.windowWidth/QUAD_SIZE;
	/**
	 * the default world offset in the x-direction in quads
	 */
	public static final double WORLD_OFFSET_X = 0.5;
	/**
	 * the default world offset in the y-direction in quads
	 */
	public static final double WORLD_OFFSET_Y = 0.5;
	
	/**
	 * the current Network Object
	 */
	public static Network net;
	
	/**
	 * The path of the games jar file
	 * this value is null until new Game() is called the first time
	 */
	public static String gamePath;
	
	/**
	 * The currently shown level
	 */
	private static Level curLevel;
	
	/**
	 * The x-position of the camera
	 */
	public static double camX = 0;
	/**
	 * The y-position of the camera
	 */
	public static double camY = 0;
	
	/**
	 * Number of rendering/update layers
	 */
	public static final int LAYERS = 10;
	/**
	 * The highest layer (drawn above everything else)
	 */
	public static final int L_HIGHEST = 0;
	/**
	 * Layer for player
	 */
	public static final int L_PLAYER = 1;
	/**
	 * layer for weapons
	 */
	public static final int L_WEAPONS = 2;
	/**
	 * layer for walls
	 */
	public static final int L_WALL = 3;
	/**
	 * effect layer 1
	 */
	public static final int L_EFFECT_1 = 4;
	/**
	 * effect layer 2
	 */
	public static final int L_EFFECT_2 = 5;
	/**
	 * layer for decoration
	 */
	public static final int L_DECO = 7;
	/**
	 * layer for floor
	 */
	public static final int L_FLOOR = 8;
	/**
	 * lowest layer (drawn under everything else)
	 */
	public static final int L_LOWEST = LAYERS-1;
	/**
	 * All active gameobjects. Will be cleared when level changes
	 */
	private static ArrayList<ArrayList<Gameobject>> gameobjects;
	/**
	 * Class for controlling the camera
	 */
	public static CameraController camController;
	
	/**
	 * Create the game
	 * There may be only one instance of this class
	 */
	public Game() {
		
		/*
		 * Get Location of Game
		 */
		
		String fileName = "/" + new File(Main.class.getProtectionDomain()
				  .getCodeSource()
				  .getLocation()
				  .getPath())
				.getName();
		
		try {
			gamePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", "");
			for(int i = 0; i < gamePath.length(); i++) {
				if(gamePath.charAt(i) == '/') {
					gamePath = gamePath.substring(0, i) + File.separator + gamePath.substring(i+1, gamePath.length());
				}
			}
			if(gamePath.endsWith("bin" + File.separator)) {
				gamePath = gamePath.substring(0, gamePath.length()-5);
			} else {
				gamePath = gamePath.substring(0, gamePath.length()-fileName.length());
			}
			gamePath = gamePath.concat(File.separator);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		/*
		 * Load textures
		 */
		
		TexManager.loadAnimation("player", gamePath + "textures\\player", 6);
		TexManager.loadAnimation("explosion", gamePath + "textures\\explosion", 20);
		
		TexManager.loadTex("grass", gamePath + "textures\\grass.jpg");
		TexManager.loadTex("wall", gamePath + "textures\\wall.png");
		TexManager.loadTex("button_normal", gamePath + "textures\\button_normal.png");
		TexManager.loadTex("button_mouseOver", gamePath + "textures\\button_mouseOver.png");
		TexManager.loadTex("button_clicked", gamePath + "textures\\button_clicked.png");
		TexManager.loadTex("ascii", gamePath + "textures\\ascii.png");
		TexManager.loadTex("playerSpawn", gamePath + "textures\\playerSpawn.png");
		TexManager.loadTex("grenade", gamePath + "textures\\grenade.png");
		TexManager.loadTex("crater", gamePath + "textures\\crater.png");
		TexManager.loadTex("reload", gamePath + "textures\\reload.png");
		TexManager.loadTex("healthPack", gamePath + "textures\\healthPack.jpg");
		TexManager.loadTex("rocket", gamePath + "textures\\rocket.png");
		TexManager.loadTex("rocketLauncher", gamePath + "textures\\rocket_launcher.png");
		TexManager.loadTex("laser", gamePath + "textures\\laser.png");
		TexManager.loadTex("grenadeLauncher", gamePath + "textures\\grenade_launcher.png");
		
		/*
		 * Execute static block of Primitives in order to prevent problems with map creator
		 */
		@SuppressWarnings("unused")
		Primitive tmp = Primitives.mapFloor;
		
		gameobjects = new ArrayList<>();
		for(int i = 0; i < LAYERS; i++) {
			gameobjects.add(new ArrayList<>());
		}
		
		curLevel = new MainMenu();
		camController = new CameraController();
		
	}
	
	/**
	 * Changes the level. Calls onClose on the removed level
	 * @param lvl the new level
	 */
	public static void changeLevel(Level lvl) {		
		curLevel.onClose();
		curLevel = lvl;
	}
	
	/**
	 * Removes all gameobjects
	 */
	public static void removeAllGameobjects() {
		for(int i = 0; i < gameobjects.size(); i++) {
			ArrayList<Gameobject> g = gameobjects.get(i);
			while (g.size() > 0) {
				g.remove(0).destroy();
			}
		}
	}
	
	/**
	 * adds a gameobject to the game
	 * @param obj the gameobject to add
	 * @param layer the layer to update and render the object on
	 */
	public static void addGameobject(Gameobject obj, int layer) {
		gameobjects.get(layer).add(obj);
	}
	
	/**
	 * Removes a gameobject from the game
	 * @param obj the gameobject
	 * @return true if this list contained the specified element
	 */
	public static boolean removeGameobject(Gameobject obj) {
		return gameobjects.remove(obj);
	}
	
	/**
	 * Get the gameobjects which have the specified component
	 * @param comp the component
	 * @return the gameobjects which have the specified component
	 */
	public static ArrayList<Gameobject> gameobjectsWith(Class<? extends Component> comp) {
		ArrayList<Gameobject> g = new ArrayList<>();
		for(int a = 0; a < gameobjects.size(); a++) {
			ArrayList<Gameobject> c = gameobjects.get(a);
			for(int i = 0; i < c.size(); i++) {
				if(c.get(i).hasComponent(comp)) g.add(c.get(i));
			}
		}
		return g;
	}
	
	/**
	 * Get an array containing all gameobjects<br>
	 * Array starts with gameobjects in the highest layer and ends with lowest layer
	 * @return array containing all gameobjects
	 */
	public static Gameobject[] getGameobjects() {
		int size = 0;
		for(int i = 0; i < gameobjects.size(); i++) {
			size+=gameobjects.get(i).size();
		}
		Gameobject[] g = new Gameobject[size];
		int c = 0;
		for(int i = gameobjects.size()-1; i >= 0; i--) {
			for(int a = 0; a < gameobjects.get(i).size() && c < g.length; a++) {
				g[c] = gameobjects.get(i).get(a);
				c++;
			}
		}
		return g;
	}
	
	/**
	 * Update the Game<br>
	 * called by Main<br>
	 * all updates should be done in here
	 * @param deltaTime time since the last update
	 */
	protected void update(double deltaTime) {
		
		curLevel.update(deltaTime);
		
		if(Game.net == null || Game.net.isServer()) {
			for(int a = 0; a < gameobjects.size(); a++) {
				ArrayList<Gameobject> g = gameobjects.get(a);
				for(int i = 0; i < g.size(); i++) {
					g.get(i).update(deltaTime);
				}
			}
		}
		
		camController.update(deltaTime);
		
		if(Game.net != null && Game.net.isServer()) {
			Game.net.updateNet(deltaTime);
		}

	}
	
	/**
	 * Render the game<br>
	 * called by Main<br>
	 * all rendering must be done in here
	 */
	protected void render() {
		
		GL11.glTranslated(-camX*QUAD_SIZE+WORLD_OFFSET_X*QUAD_SIZE, -camY*QUAD_SIZE+WORLD_OFFSET_X*QUAD_SIZE, 0);
		for(int a = 9; a >= 0; a--) {
			ArrayList<Gameobject> g = gameobjects.get(a);
			for(int i = 0; i < g.size(); i++) {
				g.get(i).render();
			}
		}
		Debug.renderDebug();
		GL11.glTranslated(camX*QUAD_SIZE-WORLD_OFFSET_X*QUAD_SIZE, camY*QUAD_SIZE-WORLD_OFFSET_X*QUAD_SIZE, 0);
		
		curLevel.render();
		
	}
	
	/**
	 * called when the mainLoop ended
	 */
	protected void onClose() {
		if(net != null) net.close();
	}
	
}
