package main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import debug.Debug;
import levels.Level;
import levels.MainMenu;
import levels.OnlineGame;
import loading.TexManager;
import map.Map;
import map.Player;
import network.Network;
import physics.Physics;

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
	 * TODO create Physics Thread in Network
	 */
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
	 * The currently shown level
	 */
	private static Level curLevel;
	
	/**
	 * The Thread calculating physics
	 */
	private Thread physicsThread;
	
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
		
		TexManager.loadTex("grass", gamePath + "textures\\grass.jpg");
		TexManager.loadTex("wall", gamePath + "textures\\wall.png");
		TexManager.loadTex("player", gamePath + "textures\\player.gif");
		
		try {
			new ServerSocket(25565).close();
			curLevel = new OnlineGame(25565);
		} catch(Exception e) {
			curLevel = new OnlineGame(InetAddress.getLoopbackAddress(), 25565);
		}
		
		//curLevel = new MainMenu();
		
	}
	
	/**
	 * Changes the level. Calls onClose on the removed level
	 * @param lvl the new level
	 */
	public void changeLevel(Level lvl) {
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
		net.close();
	}
	
}
