package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import main.Game;
import map.Player;
import physics.Physics;
import physics.Vector;

public class NetHandle {
	
	private boolean stop = false;
	
	private Thread outThread;
	private Thread inThread;
	
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	protected int playerID;
	
	public NetHandle(Socket client, ObjectOutputStream out, ObjectInputStream in) {
		this.client = client;
		this.out = out;
		this.in = in;
		outThread = new Thread(outRunnable);
		inThread = new Thread(inRunnable);
		outThread.start();
		inThread.start();
	}
	
	public void close() {
		stop = true;
	}
	
	private Runnable outRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			double last = System.nanoTime();
			
			while(!stop) {
				
				double now = System.nanoTime();
				double deltaTime = (now-last)/1000000000d;
					
				if(1/deltaTime > Network.SYNC_RATE) {
					double sleepTime = (1/Network.SYNC_RATE - deltaTime)*1000;
					try {
						Thread.sleep((long) sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					now = System.nanoTime();
					deltaTime = (now-last)/1000000000d;
				}
				last = now;
				
				try {
					
					for(int i = 0; i < Game.net.netObjects.size(); i++) {
						
						ArrayList<Object> data = new ArrayList<>();
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
				in.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	private Runnable inRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			/*
			 * TODO handle player input here
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
