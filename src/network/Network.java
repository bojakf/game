package network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import main.Game;
import physics.Vector;

/**
 * 
 * Managment class for networking
 * 
 * @author jafi2
 *
 */
public class Network {
	
	/*
	 * TODO replace Mouse deprecated methods for server clients
	 */
	
	/*
	 * TODO add client update for visuals
	 */
	
	/*
	 * TODO send new objects during runntime to clients
	 */
	
	/*
	 * ((TODO reduce CPU overhead))
	 */
	
	/*
	 * TODO remove player after disconnect
	 */
	
	/**
	 * Updates sent to the clients per second
	 */
	public static final double SYNC_RATE = 60;
	
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
	 */
	private Network(int playerID) {
		this.playerID = playerID;
		netObjects = new ArrayList<>();
		netPlayers = new ArrayList<>();
	}
	
	/**
	 * creates a server
	 * @param port the port of the server
	 * @return the Network object for interacting with the network
	 */
	public static Network createServer(int port) {
		Network r = new Network(0);
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
		Network r = new Network(client.getPlayerID());
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
	 * Clients are not allowed to call this method
	 * @param deltaTime time since last update
	 */
	public void updateNetObjects(double deltaTime) {
		
		if(isClient()) {
			new Exception("Clients cannot update objects").printStackTrace();
			return;
		}
		
		for(int i = 0; i < netObjects.size(); i++) {
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
		netObjects.add(obj);
		//TODO send Object to clients
		
	}
	
	/**
	 * Called by the server in order to add a netPlayer<br>
	 * Clients are not allowed to call this method
	 * @param obj the netPlayer to add
	 */
	public void registerNetPlayer(NetPlayer obj) {
		
		if(isClient()) {
			new Exception("Clients cannot register objects").printStackTrace();
			return;
		}
		netObjects.add(obj);
		netPlayers.add(obj);
		//TODO send Player to clients
		
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
		new Exception("No input found for player " + playerID).printStackTrace();
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
