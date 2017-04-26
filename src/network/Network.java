package network;

import java.net.InetAddress;
import java.util.ArrayList;

import map.Player;

public class Network {

	/*
	 * 
	 * Register object with id and send id before the object
	 * init send whole object
	 * then use netupdate and send object count then objects
	 * 
	 */
	
	/*
	 * FIXME independent input for players
	 * TODO replace Mouse deprecated methods for server clients
	 * TODO replace Keyboard deprecated methods for server clients
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
	
	public static final double SYNC_RATE = 60;
	
	public final int playerID;
	public ArrayList<Player> players;
	
	private NetServer server;
	private NetClient client;
	
	protected ArrayList<NetObject> netObjects;
	protected ArrayList<NetPlayer> netPlayers;
	
	private Network(int playerID) {
		this.playerID = playerID;
		netObjects = new ArrayList<>();
		netPlayers = new ArrayList<>();
	}
	
	public static Network createServer(int port) {
		Network r = new Network(0);
		r.server = new NetServer(port);
		r.client = null;
		return r;
	}
		
	public static Network connectToServer(InetAddress address, int port) {
		NetClient client = new NetClient(address, port);
		Network r = new Network(client.getPlayerID());
		r.client = client;
		r.server = null;
		return r;
	}
	
	public void renderNetObjects() {
		
		for(int i = 0; i < netObjects.size(); i++) {
			netObjects.get(i).render();
		}
		
	}
	
	public void updateNetObjects(double deltaTime) {
		
		if(isClient()) {
			new Exception("Clients cannot update objects").printStackTrace();
			return;
		}
		
		for(int i = 0; i < netObjects.size(); i++) {
			netObjects.get(i).update(deltaTime);
		}
		
	}
	
	/*
	 * Called by server after object creation
	 */
	public void registerNetObject(NetObject obj) {
		
		if(isClient()) {
			new Exception("Clients cannot register objects").printStackTrace();
			return;
		}
		netObjects.add(obj);
		//TODO send Object to clients
		
	}
	
	/*
	 * Called by server after player creation
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
	
	/*
	 * Called by client when object is loaded
	 */
	protected void addNetObject(NetObject obj) {
		netObjects.add(obj);
	}
	
	/*
	 * Called by client when player is loaded
	 */
	protected void addNetPlayer(NetPlayer obj) {
		netObjects.add(obj);
		netPlayers.add(obj);
		System.out.println(netPlayers.size());
	}
	
	public int genPlayerID() {
		return netPlayers.size();
	}
	
	public void close() {
		if(server != null) {
			server.close();
		}
		if(client != null) {
			client.close();
		}
	}
	
	public boolean isServer() {
		return playerID == 0;
	}
	
	public boolean isClient() {
		return playerID != 0;
	}
	
}
