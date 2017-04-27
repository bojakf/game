package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import debug.Debug;
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
	 * The Thread calculating physics
	 */
	private Thread physicsThread;
	
	/**
	 * Create the game
	 * There may be only one instance of this class
	 */
	public Game() {
		
		//TODO don't use static path
		TexManager.loadTex("grass", "D:\\workspace\\git\\Game\\textures\\grass.jpg");
		TexManager.loadTex("wall", "D:\\workspace\\git\\Game\\textures\\wall.png");
		TexManager.loadTex("player", "D:\\workspace\\git\\Game\\textures\\player.gif");
		
		try {
			new ServerSocket(25565).close();
			net = Network.createServer(25565);
		} catch (IOException e) {
			net = Network.connectToServer(InetAddress.getLoopbackAddress(), 25565);
		}
		
		if(net.isServer()) {
		
			physicsThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					//Max Physics update rate
					final double MAX_UPDATES_PER_SECOND = 60;
					
					double last = System.nanoTime();
					
					while(!Main.isClosing) {
					
						double now = System.nanoTime();
						double deltaTime = (now-last)/1000000000d;
							
						if(1/deltaTime > MAX_UPDATES_PER_SECOND) {
							double sleepTime = (1/MAX_UPDATES_PER_SECOND - deltaTime)*1000;
							try {
								Thread.sleep((long) sleepTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							now = System.nanoTime();
							deltaTime = (now-last)/1000000000d;
						}
						last = now;
						
						Physics.physicsUpdate(deltaTime);
						
					}				
					
				}
			}, "Physics");
			physicsThread.start();
			
			new Map();
			
			net.registerNetPlayer(new Player(0));
		
		}
		
	}
	
	/**
	 * Update the Game<br>
	 * called by Main<br>
	 * all updates should be done in here
	 * @param deltaTime time since the last update
	 */
	protected void update(double deltaTime) {
		if(net.isServer()) {
			net.updateNetObjects(deltaTime); 
		}
	}
	
	/**
	 * Render the game<br>
	 * called by Main<br>
	 * all rendering must be done in here
	 */
	protected void render() {
		
		net.renderNetObjects();
		
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
