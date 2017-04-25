package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import main.Game;
import map.Player;

public class NetClient {
	
	private boolean stop = false;
	
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Thread inThread;
	private Thread outThread;
	private int playerID = -1;
	
	protected NetClient(InetAddress address, int port) {
		
		try {
			
			client = new Socket(address, port);
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());
			
			try {
				playerID = in.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			inThread = new Thread(inRunnable);
			outThread = new Thread(outRunnable);
			
			inThread.start();
			outThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public void close() {
		stop = true;
	}
	
	private Runnable inRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			while(!stop) {
				
				try {
					
					byte command = in.readByte();
					
					if(command == NetCommands.DISCONNECT) {
						break;
					} else if(command == NetCommands.ADD_OBJECT) {
						Game.net.addNetObject((NetObject) in.readObject());
					} else if(command == NetCommands.ADD_PLAYER) {
						
						NetPlayer p = (NetPlayer) in.readObject();
						Game.net.addNetPlayer(p);
						
					} else if(command == NetCommands.UPDATE_OBJECT) {
						
						int id = in.readInt();
						int size = in.readInt();
						ArrayList<Object> data = new ArrayList<>();
						
						for(int i = 0; i < size; i++) {
							data.add(in.readObject());
						}
						
						/*
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * FIXME TODO FIXME TODO
						 * if the condition of this if statement is true this means that
						 * the id does not represent the correct object
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 * 
						 */
						if(data.size() > 3 && !(Game.net.netObjects.get(id) instanceof Player)) new Exception("Error");
						Game.net.netObjects.get(id).receiveNetUpdate(data);
						
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			
			try {
				in.close();
				out.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	private Runnable outRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			/*
			 * TODO send input information to server
			 */
			
			while(!stop) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	};
	
}
