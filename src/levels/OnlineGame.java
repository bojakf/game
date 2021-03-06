package levels;

import java.net.InetAddress;

import org.lwjgl.opengl.GL11;

import components.Player;
import gameobject.Gameobject;
import main.Game;
import main.Main;
import main.Primitives;
import map.CameraController;
import map.Map;
import network.Network;
import physics.Physics;
import physics.Vector;
import ui.Ui;
import ui.WeaponSelectUi;

/**
 * 
 * This is the the level for online Games
 * 
 * @author jafi2
 *
 */
public class OnlineGame extends Level {

	/**
	 * This value is only used by server
	 */
	private Thread physicsThread;
	
	/**
	 * The ui for the user of this application may be client or server
	 */
	private Ui ui;
	
	/**
	 * Join a server
	 * @param serverAdress the adress of the server
	 * @param port the port of the server
	 */
	public OnlineGame(InetAddress serverAdress, int port) {
		
		Game.net = Network.connectToServer(serverAdress, port);
		CameraController.mapSizeX = Game.QUADS_X*2;
		CameraController.mapSizeY = Game.QUADS_Y*2;
		
		init();
		
	}
	
	/**
	 * Create a server
	 * @param port the port of the server
	 */
	public OnlineGame(int port) {
		
		Game.net = Network.createServer(port);
		
		physicsThread = new Thread(physicsRunnable = new PhysicsRunnable(), "Physics");
		physicsThread.start();
		
		new Map();
		
		Gameobject player = Primitives.player.create(new Vector(10, 10));
		((Player)player.getComponent(Player.class)).setPlayerID(0);
		player.init();
		
		init();
		
	}
	
	private void init() {
		
		ui = new Ui();
		
		new WeaponSelectUi(ui, Game.net.playerID);
		
	}
	
	@Override
	public void update(double deltaTime) {
		ui.update(deltaTime);
	}

	@Override
	public void render() {
		
		GL11.glTranslated(-Game.camX*Game.QUAD_SIZE+Game.WORLD_OFFSET_X*Game.QUAD_SIZE, -Game.camY*Game.QUAD_SIZE+Game.WORLD_OFFSET_X*Game.QUAD_SIZE, 0);
		Physics.drawColliders();
		GL11.glTranslated(Game.camX*Game.QUAD_SIZE-Game.WORLD_OFFSET_X*Game.QUAD_SIZE, Game.camY*Game.QUAD_SIZE-Game.WORLD_OFFSET_X*Game.QUAD_SIZE, 0);
	
		ui.render();
		
	}

	@Override
	public void onClose() {
		Game.net.close();
		physicsRunnable.stop();
	}
	
	/**
	 * The runnable of the physics thread
	 * TODO move this to physics thread
	 */
	private PhysicsRunnable physicsRunnable;
	/**
	 * 
	 * Used to run a physics thread
	 * 
	 * @author jafi2
	 *
	 */
	private class PhysicsRunnable implements Runnable {
		
		/**
		 * Should the physics thread stop
		 */
		private boolean stop = false;
		
		@Override
		public void run() {
			
			//Max Physics update rate
			final double MAX_UPDATES_PER_SECOND = 60;
			
			double last = System.nanoTime();
			
			while(!Main.isClosing && !stop) {
			
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
		
		/**
		 * Stops the physics thread
		 */
		public void stop() {
			stop = true;
		}
		
	};

}
