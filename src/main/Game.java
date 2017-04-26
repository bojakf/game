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
	
	public static final double QUADS_Y = 15d;
	public static final double QUAD_SIZE = Main.windowHeight/QUADS_Y;
	public static final double QUADS_X = Main.windowWidth/QUAD_SIZE;
	
	public static Network net;
	
	private Map map;
	private Thread physicsThread;
	
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
			
			map = new Map();
			
			net.registerNetPlayer(new Player(0));
		
		}
		
	}
	
	protected void update(double deltaTime) {
		
		if(net.isServer()) {
			net.updateNetObjects(deltaTime); 
		}
		
	}
	
	protected void render() {
		
		net.renderNetObjects();
		
		Physics.drawColliders();
		
		Debug.renderDebug();
		
	}
	
	protected void onClose() {
		net.close();
	}
	
}
