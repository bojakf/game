package main;

import java.io.File;
import java.net.URISyntaxException;

import debug.Debug;
import levels.Level;
import levels.MainMenu;
import loading.TexManager;
import network.Network;
import physics.Physics;
import rendering.AnimatedTexture;

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
	 * TODO add option to disable vsync
	 */
	/*
	 * TODO log files
	 */
	/*
	 * TODO change network input handling (client has to use server key bindings)
	 */
	/*
	 * TODO add taskbar icon
	 */
	/*
	 * TODO add main Menu
	 */
	/*
	 * TODO add map creator level
	 */
	/*
	 * TODO server gather net object information only once and send to all clients
	 */
	/*
	 * TODO maybe add option to use multicast to send server updates to the clients
	 */
	/*
	 * TODO use a default texture for the floor and not many objects. Set the size at which the texture should be repeated
	 */
	/*
	 * TODO mipmaping
	 */
	/*
	 * TODO texture packs
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
	 * the current Network Object
	 */
	public static Network net;
	
	/**
	 * The path of the games jar file
	 * this value is null until new Game() is called the first time
	 */
	public static String gamePath;
	
	/**
	 * The animation for the player
	 */
	public static AnimatedTexture playerTex;
	
	/**
	 * The currently shown level
	 */
	private static Level curLevel;
	
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
		
		if(playerTex == null) {
			playerTex = new AnimatedTexture("textures\\player", 6);
		}
		
		TexManager.loadTex("grass", gamePath + "textures\\grass.jpg");
		TexManager.loadTex("wall", gamePath + "textures\\wall.png");
		TexManager.loadTex("button_normal", gamePath + "textures\\button_normal.png");
		TexManager.loadTex("button_mouseOver", gamePath + "textures\\button_mouseOver.png");
		TexManager.loadTex("button_clicked", gamePath + "textures\\button_clicked.png");
		TexManager.loadTex("ascii", gamePath + "textures\\ascii.png");
		TexManager.loadTex("playerSpawn", gamePath + "textures\\playerSpawn.png");
		TexManager.loadTex("grenade", gamePath + "textures\\grenade.png");
		
		curLevel = new MainMenu();
		
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
	 * Update the Game<br>
	 * called by Main<br>
	 * all updates should be done in here
	 * @param deltaTime time since the last update
	 */
	protected void update(double deltaTime) {
		curLevel.update(deltaTime);
	}
	
	/**
	 * Render the game<br>
	 * called by Main<br>
	 * all rendering must be done in here
	 */
	protected void render() {
		
		curLevel.render();
		
		Physics.drawColliders();
		Debug.renderDebug();
		
	}
	
	/**
	 * called when the mainLoop ended
	 */
	protected void onClose() {
		if(net != null) net.close();
	}
	
}
