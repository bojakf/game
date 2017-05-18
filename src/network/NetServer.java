package network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import components.FinalNetComponent;
import components.NetComponent;
import loading.MultiOutStream;
import main.Game;

/**
 * 
 * This class is used to create a server and send informations to clients
 * 
 * @author jafi2
 *
 */
public class NetServer {
	
	/*
	 * FIXME improve sending (only one netComponent per gameobject)
	 */
	
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
	private ArrayList<NetComponent> sendQueue = new ArrayList<>();
	/**
	 * Contains the finalNetComponents queued for sending to the client
	 */
	private ArrayList<FinalNetComponent> finalSendQueue = new ArrayList<>();
	/**
	 * Contains all objects queued for removal
	 */
	private ArrayList<NetComponent> removeQueue = new ArrayList<>();
	/**
	 * Contains all finalNetComponents queued for removal
	 */
	private ArrayList<FinalNetComponent> finalRemoveQueue = new ArrayList<>();
	
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
	 * Sends a new netComponent to the client
	 * @param obj the netComponent
	 */
	protected void sendNetComponent(NetComponent obj) {
		if(handles.size() == 0)
			Game.net.netComponents.add(obj);
		else {
			sendQueue.add(obj);
		}
	}
	
	/**
	 * Sends a new FinalNetComponent to the client
	 * @param obj the finalNetComponent
	 */
	protected void sendFinalNetComponent(FinalNetComponent obj) {
		if(handles.size() == 0)
			Game.net.finalNetComponents.add(obj);
		else {
			finalSendQueue.add(obj);
		}
	}
	
	/**
	 * Removes a net object from the game
	 * @param id the net object to remove
	 */
	protected void removeNetObject(NetComponent id) {
		if(handles.size() == 0) {
			Game.net.netComponents.remove(id);
		} else {
			removeQueue.add(id);
		}
	}
	
	/**
	 * Removes a finalNetComponent from the game
	 * @param id the net object to remove
	 */
	protected void removeFinalNetObject(FinalNetComponent id) {
		if(handles.size() == 0) {
			Game.net.finalNetComponents.remove(id);
		} else {
			finalRemoveQueue.add(id);
		}
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
					
					BufferedOutputStream o = new BufferedOutputStream(client.getOutputStream());
					ObjectOutputStream out = new ObjectOutputStream(o);
					ObjectInputStream in = new ObjectInputStream(client.getInputStream());
					
					/*
					 * Generate player
					 */
					int playerID = Game.net.createPlayer();
					
					out.writeInt(playerID);
					
					
					for(int i = 0; i < Game.net.netComponents.size(); i++) {
						out.writeByte(NetCommands.ADD_OBJECT);
						out.writeObject(Game.net.netComponents.get(i));
					}
					
					for(int i = 0; i < Game.net.finalNetComponents.size(); i++) {
						out.writeByte(NetCommands.ADD_FINAL_COMPONENT);
						out.writeObject(Game.net.finalNetComponents.get(i));
					}
					
					out.flush();
					out.reset();
					
					NetHandle handle = new NetHandle(client, out, in);
					handle.playerID = playerID;
					handles.add(handle);
					outAddQueue.add(o);
					
				
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
//				System. out.println(1/deltaTime);
				/*
				 * Add new clients to update multiStream
				 */
				while(outAddQueue.size() > 0) {
					multiOut.out.add(outAddQueue.remove(0));
				}
				
				/*
				 * FIXME exception aborts update for all clients @see MultiOutStream
				 */
				/*
				 * TODO improve performance of this block
				 */
				try {
					
					/*
					 * remove objects from client and server
					 */
					while(removeQueue.size() > 0) {
						NetComponent remove = removeQueue.remove(0);
						int id = Game.net.netComponents.indexOf(remove);
						if(id == -1) continue;
						out.writeByte(NetCommands.REMOVE_OBJECT);
						out.writeInt(id);
						out.flush();
						Game.net.netComponents.remove(remove);
					}
					
					/*
					 * remove finalNetComponents
					 */
					while(finalRemoveQueue.size() > 0) {
						FinalNetComponent remove = finalRemoveQueue.remove(0);
						int id = Game.net.finalNetComponents.indexOf(remove);
						if(id == -1) continue;
						out.writeByte(NetCommands.REMOVE_FINAL_COMPONENT);
						out.writeInt(id);
						out.flush();
						Game.net.finalNetComponents.remove(id);
					}
					
					/*
					 * send new objects to client 
					 */
					while(sendQueue.size() > 0) {
						
						NetComponent obj = sendQueue.remove(0);
						out.writeByte(NetCommands.ADD_OBJECT);
						Game.net.netComponents.add(obj);
						out.writeObject(obj);
						out.flush();
						
					}
					
					/*
					 * Send new finalNetComponents
					 */
					while(finalSendQueue.size() > 0) {
						
						FinalNetComponent obj = finalSendQueue.remove(0);
						out.writeByte(NetCommands.ADD_FINAL_COMPONENT);
						Game.net.finalNetComponents.add(obj);
						out.writeObject(obj);
						out.flush();
						
					}
					
//					long c = System.nanoTime();
					/*
					 * update objects at client
					 */
					for(int i = 0; i < Game.net.netComponents.size(); i++) {
						
//						c = System.nanoTime();
						
						/*
						 * Update existing objects
						 */
						ArrayList<Serializable> data = new ArrayList<>();
						Game.net.netComponents.get(i).sendNetUpdate(data);
						
						out.writeByte(NetCommands.UPDATE_OBJECT);
						out.writeInt(i);
						out.writeInt(data.size());
						
//						System.out.println("init      " + (System.nanoTime()-c)+"ns");
						
						
						for(int a = 0; a < data.size(); a++) {
//							c = System.nanoTime();
							out.writeObject(data.get(a));
//							System.out.println("write     " + (System.nanoTime()-c)+"ns");
						}
//						c = System.nanoTime();
						out.flush();
//						System.out.println("flush     " + (System.nanoTime()-c)+"ns");
						
					}
					
					/*
					 * Reset output stream in order to prevent
					 * referencing already sent objects
					 */
					
//					c = System.nanoTime();
					
					out.flush();
					out.reset();
					
//					System.out.println("final     " + (System.nanoTime()-c)+"ns");
					
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
