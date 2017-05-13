package network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import components.NetComponent;
import main.Game;
import main.Primitives;
import physics.Vector;
import components.Player;
import gameobject.Gameobject;

/**
 * 
 * Managment class for networking
 * 
 * @author jafi2
 *
 */
public class Network {
	
	/*
	 * TODO rework networking
	 */
	
	/*
	 * TODO do not update correct information compare with cloned objects
	 */
	
	/*
	 * TODO remove player after disconnect
	 */
	
	/**
	 * Default number of Updates sent to the clients per second
	 */
	public static final double DEFAULT_SYNC_RATE = 60;
	/**
	 * Updates sent to the clients per second
	 */
	public final double SYNC_RATE;
	
	/**
	 * The default port of the server
	 */
	public static final int DEFAULT_PORT = 25565;
	
	/**
	 * The id of the local player
	 */
	public final int playerID;
	
	/**
	 * the server Null if this is a client
	 */
	private NetServer server;
	/**
	 * the client Null if this is a server
	 */
	private NetClient client;
	
	/**
	 * All objects updated through network, also contains netPlayers
	 */
	protected ArrayList<NetComponent> netObjects;
	
	/**
	 * initializes Network
	 * @param playerID the id of the local user, for server always 0
	 * @param syncRate only for server. number of updates sent to clients per second
	 */
	private Network(int playerID, double syncRate) {
		this.playerID = playerID;
		netObjects = new ArrayList<>();
		SYNC_RATE = syncRate;
	}
	
	
	
	
	/**
	 * creates a server
	 * @param port the port of the server
	 * @return the Network object for interacting with the network
	 */
	public static Network createServer(int port) {
		return createServer(port, DEFAULT_SYNC_RATE);
	}
	
	/**
	 * creates a server
	 * @param port the port of the server
	 * @param syncRate the number of updates sent to clients per second.
	 * @return the Network object for interacting with the network
	 */
	public static Network createServer(int port, double syncRate) {
		Network r = new Network(0, syncRate);
		r.server = new NetServer(port);
		r.client = null;
		return r;
	}
	
	/**
	 * Connects to an existing server
	 * @param address ip adress of the server
	 * @param port port of the server
	 * @return the Network object for interacting with the network
	 */
	public static Network connectToServer(InetAddress address, int port) {
		NetClient client = new NetClient(address, port);
		Network r = new Network(client.getPlayerID(), DEFAULT_SYNC_RATE);
		r.client = client;
		r.server = null;
		Game.net = r;
		client.init();
		return r;
	}
	
	/**
	 * Updates the network instance (removes destroyed objects)
	 * @param deltaTime time since last update
	 */
	public void updateNet(double deltaTime) {
		
		if(isClient()) {
			new Exception("Clients cannot update objects").printStackTrace();
			return;
		}
		
		for(int i = 0; i < netObjects.size(); i++) {
			if(netObjects.get(i).isDestroyed()) {
				server.removeNetObject(netObjects.get(i));
				continue;
			}
		}
		
	}
	
	/**
	 * Adds a Object to the network
	 * @param obj the object
	 */
	public void add(NetComponent obj) {
		
		if(playerID == 0) {
			server.sendNetObject(obj);
		} else {
			netObjects.add(obj);
			obj.getParent().init();
		}
		
	}
	
	/**
	 * Creates a new player
	 * @return the id of the player created
	 */
	public int createPlayer() {
		
		int playerID = Game.gameobjectsWith(Player.class).size();
		
		Gameobject player = Primitives.player.create(new Vector(10, 10));
		((Player)player.getComponent(Player.class)).setPlayerID(playerID);
		player.init();
		
		return playerID;
		
	}
	
	/**
	 * Returns a HashMap with all known states of keyboard keys for the given player<br>
	 * Clients may not call this method<br>
	 * Method must not be called with the local player's id
	 * @param playerID the id of the player
	 * @return the HashMap use GLFW.GLFW_KEY_keyname as key to get GLFW key state
	 */
	public HashMap<Integer, Integer> getKeyStates(int playerID) {
		if(playerID == this.playerID) {
			new Exception("Use input.Keyboard for local key polling").printStackTrace();
			return null;
		}
		for(int i = 0; i < server.handles.size(); i++) {
			NetHandle n = server.handles.get(i);
			if(n.playerID == playerID) {
				return n.keyStates;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the last reported mouse position for the given player<br>
	 * Clients may not call this method<br>
	 * Method must not be called with the local player's id
	 * @param playerID the id of the player
	 * @return the position of the mouse
	 */
	public Vector getMousePosition(int playerID) {
		if(playerID == this.playerID) {
			new Exception("Use input.Mouse for local mouse position").printStackTrace();
			return null;
		}
		for(int i = 0; i < server.handles.size(); i++) {
			NetHandle n = server.handles.get(i);
			if(n.playerID == playerID) {
				return n.mousePos;
			}
		}
		new Exception("No mouse pos found for player " + playerID).printStackTrace();
		return null;
	}
	
	/**
	 * Close the client or the server
	 */
	public void close() {
		if(server != null) {
			server.close();
		}
		if(client != null) {
			client.close();
		}
	}
	
	/**
	 * Is this a server
	 * @return server?
	 */
	public boolean isServer() {
		return playerID == 0;
	}
	
	/**
	 * Is this a client
	 * @return client?
	 */
	public boolean isClient() {
		return playerID != 0;
	}
	
}
