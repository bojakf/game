package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import input.Keyboard;
import input.Mouse;
import physics.Vector;

/**
 * 
 * Handles one client on the server
 * 
 * @author jafi2
 *
 */
public class NetHandle {
	
	/**
	 * True if the client disconnected
	 */
	private boolean stop = false;
	
	/**
	 * Thread handling input from the client
	 */
	private Thread inThread;
	
	/**
	 * Client socket
	 */
	protected Socket client;
	/**
	 * the output stream to the client
	 */
	protected ObjectOutputStream out;
	/**
	 * the input stream from the client
	 */
	protected ObjectInputStream in;
	
	/**
	 * the id of the player this client represents
	 */
	protected int playerID;
	/**
	 * Contains the state of all keys transmitted by the client
	 */
	protected HashMap<Integer, Integer> keyStates = new HashMap<>();
	/**
	 * Contains the last mouse position transmitted by the client
	 */
	protected Vector mousePos = new Vector();
	
	/**
	 * Creates a new netHandle starts two threads for IO
	 * @param client the clients socket
	 * @param out the output thread for the client
	 * @param in the input thread for the client
	 */
	public NetHandle(Socket client, ObjectOutputStream out, ObjectInputStream in) {
		this.client = client;
		this.out = out;
		this.in = in;
		inThread = new Thread(inRunnable, "Client " + playerID + " in");
		inThread.start();
	}
	
	/**
	 * Closes the connection to the client
	 */
	public void close() {
		stop = true;
	}
	
	/**
	 * the runnable for input operations
	 */
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
