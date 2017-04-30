package levels;

import java.net.InetAddress;

import main.Game;
import main.Main;
import map.Map;
import map.Player;
import network.Network;
import physics.Physics;

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
	 * Join a server
	 * @param serverAdress the adress of the server
	 * @param port the port of the server
	 */
	public OnlineGame(InetAddress serverAdress, int port) {
		
		Game.net = Network.connectToServer(serverAdress, port);
		
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
		
		Game.net.registerNetPlayer(new Player(0));
		
	}
	
	@Override
	public void update(double deltaTime) {
		if(Game.net.isServer()) {
			Game.net.updateNetObjects(deltaTime); 
		}
	}

	@Override
	public void render() {
		Game.net.renderNetObjects();
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
	private class PhysicsRunnable implements Runnable {
		
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
