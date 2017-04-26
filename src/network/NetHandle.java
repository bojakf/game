package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import input.Keyboard;
import input.Mouse;
import main.Game;
import physics.Vector;

public class NetHandle {
	
	private boolean stop = false;
	
	private Thread outThread;
	private Thread inThread;
	
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	protected int playerID;
	protected HashMap<Integer, Integer> keyStates = new HashMap<>();
	protected Vector mousePos = new Vector();
	
	public NetHandle(Socket client, ObjectOutputStream out, ObjectInputStream in) {
		this.client = client;
		this.out = out;
		this.in = in;
		outThread = new Thread(outRunnable, "Client " + playerID + " out");
		inThread = new Thread(inRunnable, "Client " + playerID + " in");
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
			
			while(!stop) {
				
				try {
					
					
					byte command = in.readByte();
					
					if(command == NetCommands.CHAR_INPUT) {
						Keyboard.runOnChar(in.readChar(), playerID);
					} else if(command == NetCommands.KEY_DOWN) {
						int key = in.readInt();
						keyStates.put(key, GLFW.GLFW_PRESS);
						Keyboard.runOnKeyDown(key, in.readInt(), playerID);
					} else if(command == NetCommands.KEY_REPEAT) {
						Keyboard.runOnKeyRepeat(in.readInt(), in.readInt(), playerID);
					} else if(command == NetCommands.KEY_UP) {
						int key = in.readInt();
						keyStates.put(key, GLFW.GLFW_RELEASE);
						Keyboard.runOnKeyUp(key, in.readInt(), playerID);
					} else if(command == NetCommands.MOUSE_MOVE) {
						double x = in.readDouble();
						double y = in.readDouble();
						Mouse.runOnMove(mousePos.x == -1 ? 0 : x-mousePos.x, mousePos.y == -1 ? 0 : y-mousePos.y, playerID);
						mousePos.x = x;
						mousePos.y = y;
						System.out.println(mousePos);
					} else if(command == NetCommands.MOUSE_ENTER) {
						Mouse.runOnEnter(playerID);
					} else if(command == NetCommands.MOUSE_EXIT) {
						Mouse.runOnExit(playerID);
						mousePos.x = -1;
						mousePos.y = -1;
					} else if(command == NetCommands.MOUSE_SCROLL) {
						Mouse.runOnScroll(in.readDouble(), playerID);
					} else if(command == NetCommands.MOUSE_RELEASE) {
						Mouse.runOnRelease(in.readInt(), in.readInt(), playerID);
					} else if(command == NetCommands.MOUSE_PRESS) {
						Mouse.runOnPress(in.readInt(), in.readInt(), playerID);
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				
			}
			
		}
	};
	
}
