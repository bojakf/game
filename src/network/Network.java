package network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import main.Game;
import map.PlayerSpawn;
import physics.Vector;
import player.Player;

/**
 * 
 * Managment class for networking
 * 
 * @author jafi2
 *
 */
public class Network {
	
	/*
	 * TODO rework networking (after component system)
	 */
	
	/*
	 * TODO do not update correct information compare with cloned objects
	 * TODO bundle output to one thread (without initialization)
	 * http://stackoverflow.com/questions/7987395/how-to-write-data-to-two-java-io-outputstream-objects-at-once
	 */
	
	/*
	 * TODO remove player after disconnect
	 */
	
	/*
	 * TODO automatically differentiate between netPlayer and netObject and between server and client in on method call
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
	protected ArrayList<NetObject> netObjects;
	/**
	 * All players on the server
	 */
	protected ArrayList<NetPlayer> netPlayers;
	
	/**
	 * initializes Network
	 * @param playerID the id of the local user, for server always 0
	 * @param syncRate only for server. number of updates sent to clients per second
	 */
	private Network(int playerID, double syncRate) {
		this.playerID = playerID;
		netObjects = new ArrayList<>();
		netPlayers = new ArrayList<>();
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
	 * Renders all network Objects, should be called once per frame
	 */
	public void renderNetObjects() {
		
		for(int i = 0; i < netObjects.size(); i++) {
			netObjects.get(i).render();
		}
		
	}
	
	/**
	 * Updates all network Objects, should be called once per frame on the server.<br>
	 * Clients are not allowed to call this method.<br>
	 * netObject whose colliders are destroyed are queued for removal in this method
	 * @param deltaTime time since last update
	 */
	public void updateNetObjects(double deltaTime) {
		
		if(isClient()) {
			new Exception("Clients cannot update objects").printStackTrace();
			return;
		}
		
		for(int i = 0; i < netObjects.size(); i++) {
			if(netObjects.get(i).isPendingDestroy()) {
				server.removeNetObject(netObjects.get(i));
				continue;
			}
			netObjects.get(i).update(deltaTime);
		}
		
	}
	
	/**
	 * Called by the server in order to add a netObject<br>
	 * Clients are not allowed to call this method
	 * @param obj the netObject to add
	 */
	public void registerNetObject(NetObject obj) {
		
		if(isClient()) {
			new Exception("Clients cannot register objects").printStackTrace();
			return;
		}
		
		server.sendNetObject(obj);
		
	}
	
	/**
	 * Called by the server in order to add a netPlayer<br>
	 * Also spawns the player at his spawnpoint if there is one<br>
	 * Clients are not allowed to call this method
	 * @param obj the netPlayer to add
	 */
	public void registerNetPlayer(NetPlayer obj) {
		
		if(isClient()) {
			new Exception("Clients cannot register objects").printStackTrace();
			return;
		}
		
		/*
		 * TODO improve this
		 */
		for(int i = 0; i < netObjects.size(); i++) {
			NetObject c = netObjects.get(i);
			if(c instanceof PlayerSpawn && 
					((PlayerSpawn)c).getPlayerID() == obj.getPlayerID()) {
				((Player)obj).setPosition(((PlayerSpawn) c).getPosition());
				((Player)obj).spawn = (PlayerSpawn)c;
			}
		}
		
		server.sendNetObject(obj);
		
	}
	
	/**
	 * Adds loaded netObject to netObject list<br>
	 * Servers may not call this method
	 * @param obj the netObject to add
	 */
	protected void addNetObject(NetObject obj) {
		netObjects.add(obj);
	}
	
	/**
	 * Adds loaded netPlayer to netPlayer list<br>
	 * Servers may not call this method
	 * @param obj the netPlayer to add
	 */
	protected void addNetPlayer(NetPlayer obj) {
		netObjects.add(obj);
		netPlayers.add(obj);
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
	 * Generates a new player ID<br>
	 * may return the same number more than once if no player has been created since the last call
	 * @return the playerID
	 */
	/*
	 * TODO fix this may return the same number more than once if no player has been created since the last call
	 */
	public int genPlayerID() {
		return netPlayers.size();
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
