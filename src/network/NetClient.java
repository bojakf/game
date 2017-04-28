package network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import input.Keyboard;
import input.Keyboard.CharListener;
import input.Keyboard.KeyListener;
import input.Mouse;
import input.Mouse.MouseListener;
import input.Mouse.MouseMotionListener;
import main.Game;
import physics.Vector;

/**
 * 
 * Manages Connection to server from client side
 * 
 * @author jafi2
 *
 */
public class NetClient {
	
	/**
	 * True if the client is Disconnecting
	 */
	private boolean stop = false;
	
	/**
	 * the socket of the client
	 */
	private Socket client;
	/**
	 * the output stream to the server
	 */
	private ObjectOutputStream out;
	/**
	 * the input stream from the server
	 */
	private ObjectInputStream in;
	/**
	 * thread for handling input
	 */
	private Thread inThread;
	/**
	 * the id of the client
	 */
	private int playerID = -1;
	
	/**
	 * keyListener for server
	 */
	private KeyListener keyListener;
	/**
	 * charListener for server
	 */
	private CharListener charListener;
	/**
	 * mosueListener for server
	 */
	private MouseListener mouseListener;
	/**
	 * mouseMotionListener for server
	 */
	private MouseMotionListener mouseMotionListener;
	
	/**
	 * Create the connection to the server also retrieves the palyerID for the server
	 * @param address the ip adress of the server
	 * @param port the port number of the server
	 */
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
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Start listening to the server and sending input information to the server
	 */
	public void init() {
		
		inThread.start();
		
		keyListener = new KeyListener() {
			
			@Override
			public void onKeyUp(int keycode, int modifiers) {
				try {
					out.writeByte(NetCommands.KEY_UP);
					out.writeInt(keycode);
					out.writeInt(modifiers);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onKeyRepeat(int keycode, int modifiers) {
				try {
					out.writeByte(NetCommands.KEY_REPEAT);
					out.writeInt(keycode);
					out.writeInt(modifiers);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onKeyDown(int keycode, int modifiers) {
				try {
					out.writeByte(NetCommands.KEY_DOWN);
					out.writeInt(keycode);
					out.writeInt(modifiers);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		charListener = new CharListener() {
			
			@Override
			public void onChar(char input) {
				try {
					out.writeByte(NetCommands.CHAR_INPUT);
					out.writeChar(input);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		mouseListener = new MouseListener() {
			
			@Override
			public void onScroll(double delta) {
				try {
					out.writeByte(NetCommands.MOUSE_SCROLL);
					out.writeDouble(delta);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onRelease(int button, int modifiers) {
				try {
					out.writeByte(NetCommands.MOUSE_RELEASE);
					out.writeInt(button);
					out.writeInt(modifiers);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onPress(int button, int modifiers) {
				try {
					out.writeByte(NetCommands.MOUSE_PRESS);
					out.writeInt(button);
					out.writeInt(modifiers);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		mouseMotionListener = new MouseMotionListener() {
			
			@Override
			public void onMove(double dx, double dy) {
				try {
					out.writeByte(NetCommands.MOUSE_MOVE);
					Vector xy = Mouse.xy(playerID);
					out.writeDouble(xy.x);
					out.writeDouble(xy.y);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onExit() {
				try {
					out.writeByte(NetCommands.MOUSE_EXIT);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onEnter() {
				try {
					out.writeByte(NetCommands.MOUSE_ENTER);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		Keyboard.addKeyListener(keyListener, playerID);
		Keyboard.addCharListener(charListener, playerID);
		Mouse.addMouseButtonListener(mouseListener, playerID);
		Mouse.addMouseMotionListener(mouseMotionListener, playerID);
		
	}
	
	/**
	 * get the id of the client
	 * @return the id
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/**
	 * Close connection to server
	 */
	public void close() {
		stop = true;
	}
	
	/**
	 * Runnable for retrieving data from server
	 */
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
						ArrayList<Serializable> data = new ArrayList<>();
						
						for(int i = 0; i < size; i++) {
							data.add((Serializable)in.readObject());
						}
						
						Game.net.netObjects.get(id).receiveNetUpdate(data);
						
					} else if(command == NetCommands.REMOVE_OBJECT) {
						
						int id = in.readInt();
						NetObject obj = Game.net.netObjects.get(id);
						if(obj instanceof NetPlayer) {
							Game.net.netPlayers.remove(obj);
						}
						Game.net.netObjects.remove(id);
						
					}
					
					
				} catch (EOFException e) {
					e.printStackTrace();
					break;
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
	
}
