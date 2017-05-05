package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import loading.MultiOutStream;
import main.Game;
import player.Player;

/**
 * 
 * This class is used to create a server and send informations to clients
 * 
 * @author jafi2
 *
 */
public class NetServer {
	
	/**
	 * Thread for accepting Clients
	 */
	private Thread acceptThread;
	/**
	 * Thread for sending update information to clients
	 */
	private Thread connectionThread;
	/**
	 * Socket of server
	 */
	private ServerSocket serverSocket;
	/**
	 * True if the server is stopping or has already stopped
	 */
	private boolean stop = false;
	
	/**
	 * All handels for clients
	 */
	protected ArrayList<NetHandle> handles;
	/**
	 * Queue for adding clients to multiOut stream
	 */
	private ArrayList<OutputStream> outAddQueue;
	/**
	 * The output stream for the out stream
	 */
	private MultiOutStream multiOut;
	/**
	 * The stream the server writes update information to
	 */
	private ObjectOutputStream out;
	
	/**
	 * Contains the objects queued for sending to the client
	 */
	private ArrayList<NetObject> sendQueue = new ArrayList<>();
	/**
	 * Contains all objects queued for removal
	 */
	private ArrayList<NetObject> removeQueue = new ArrayList<>();
	
	/**
	 * Create the server
	 * @param port the port of the server
	 */
	protected NetServer(int port) {
		
		handles = new ArrayList<>();
		outAddQueue = new ArrayList<>();
		
		/*
		 * Create stream for distributing update information
		 */
		multiOut = new MultiOutStream();
		try {
			out = new ObjectOutputStream(multiOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		acceptThread = new Thread(acceptRunnable, "Server");
		acceptThread.start();
	
		connectionThread = new Thread(connectionRunnable, "Server-Client Connection");
		connectionThread.start();
		
	}
	
	/**
	 * Close the server
	 */
	public void close() {
		stop = true;
		for(int i = 0; i < handles.size(); i++) {
			handles.get(i).close();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		serverSocket.close();
		super.finalize();
	}
	
	/**
	 * Sends a new object to the client
	 * @param obj the object
	 */
	protected void sendNetObject(NetObject obj) {
		if(handles.size() == 0) {
			if(obj instanceof Player)
				Game.net.netPlayers.add(((NetPlayer)obj));
			Game.net.netObjects.add(obj);
		} else
			sendQueue.add(obj);
	}
	
	/**
	 * Removes a net object from the game
	 * @param id the net object to remove
	 */
	protected void removeNetObject(NetObject id) {
		removeQueue.add(id);
	}
	
	/**
	 * Runnable for accepting new clients
	 */
	private Runnable acceptRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			while(!stop) {
				
				try {
				
					Socket client = serverSocket.accept();
					
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(client.getInputStream());
					
					/*
					 * Generate player
					 */
					int playerID = Game.net.genPlayerID();
					
					out.writeInt(playerID);
					Game.net.registerNetPlayer(new Player(playerID));
					
					
					for(int i = 0; i < Game.net.netObjects.size(); i++) {
						if(Game.net.netObjects.get(i) instanceof NetPlayer) 
							out.writeByte(NetCommands.ADD_PLAYER);
						else
							out.writeByte(NetCommands.ADD_OBJECT);
						out.writeObject(Game.net.netObjects.get(i));
					}
					
					NetHandle handle = new NetHandle(client, out, in);
					handle.playerID = playerID;
					handles.add(handle);
					outAddQueue.add(handle.out);
					
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	};
	
	/**
	 * Runnable for sending information to all clients
	 */
	private Runnable connectionRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			double last = System.nanoTime();
			
			while(!stop) {
				
				double now = System.nanoTime();
				double deltaTime = (now-last)/1000000000d;
					
				if(1/deltaTime > Game.net.SYNC_RATE) {
					double sleepTime = (1/Game.net.SYNC_RATE - deltaTime)*1000;
					try {
						Thread.sleep((long) sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					now = System.nanoTime();
					deltaTime = (now-last)/1000000000d;
				}
				last = now;
				
				/*
				 * Add new clients to update multiStream
				 */
				while(outAddQueue.size() > 0) {
					multiOut.out.add(outAddQueue.remove(0));
				}
				
				/*
				 * FIXME exception aborts update for all clients @see MultiOutStream
				 */
				try {
					
					/*
					 * remove objects from client and server
					 */
					while(removeQueue.size() > 0) {
						
						out.writeByte(NetCommands.REMOVE_OBJECT);
						NetObject remove = removeQueue.remove(0);
						out.writeInt(Game.net.netObjects.indexOf(remove));
						Game.net.netObjects.remove(remove);
						if(remove instanceof NetPlayer) {
							Game.net.netPlayers.remove(remove);
						}
						
					}
					
					/*
					 * send new objects to client 
					 */
					while(sendQueue.size() > 0) {
						
						NetObject obj = sendQueue.remove(0);
						if(obj instanceof NetPlayer) {
							out.writeByte(NetCommands.ADD_PLAYER);
							Game.net.netPlayers.add((NetPlayer)obj);
						} else
							out.writeByte(NetCommands.ADD_OBJECT);
						Game.net.netObjects.add(obj);
						out.writeObject(obj);
						
					}
					
					/*
					 * update objects at client
					 */
					for(int i = 0; i < Game.net.netObjects.size(); i++) {
						
						/*
						 * Update existing objects
						 */
						ArrayList<Serializable> data = new ArrayList<>();
						Game.net.netObjects.get(i).sendNetUpdate(data);
						
						out.writeByte(NetCommands.UPDATE_OBJECT);
						out.writeInt(i);
						out.writeInt(data.size());
						
						for(int a = 0; a < data.size(); a++) {
							out.writeObject(data.get(a));
						}
						
					}
					
					/*
					 * Reset output stream in order to prevent
					 * referencing already sent objects
					 */
					out.flush();
					out.reset();
					
				} catch (IOException e) {
					e.printStackTrace();
					//FIXME don't handle exception like this
					if(e instanceof SocketException) break;
				}
				
			}
			
			try {
				out.writeByte(NetCommands.DISCONNECT);
				out.close();
				for(int i = 0; i < handles.size(); i++) {
					handles.get(i).in.close();
					handles.get(i).client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	};
	
}
