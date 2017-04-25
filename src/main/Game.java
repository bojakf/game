package main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.lwjgl.opengl.GL11;

import debug.Debug;
import loading.TexManager;
import map.Map;
import map.MapFloor;
import map.Player;
import network.Network;
import physics.Collider;
import physics.Physics;
import physics.Vector;

public class Game {

	/*
	 * TODO handle rendering completely in Network
	 * TODO create Physics Thread in Network
	 */
	
	public static final double QUADS_Y = 15d;
	public static final double QUAD_SIZE = Main.windowHeight/QUADS_Y;
	public static final double QUADS_X = Main.windowWidth/QUAD_SIZE;
	
	public static Network net;
	
	private Map map;
	private Thread physicsThread;
	
	public Game() {
		
		TexManager.loadTex("grass", "D:\\workspace\\JustAGame\\textures\\grass.jpg");
		TexManager.loadTex("wall", "D:\\workspace\\JustAGame\\textures\\wall.png");
		TexManager.loadTex("player", "D:\\workspace\\JustAGame\\textures\\player.gif");
		
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
			});
			physicsThread.start();
			
			map = new Map();
			
			net.registerNetPlayer(new Player(0));
		
		}
		
	}
	
	protected void update(double deltaTime) {
		
		net.updateNetObjects(deltaTime); 
		
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
