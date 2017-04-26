package network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class NetClient {
	
	private boolean stop = false;
	
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Thread inThread;
	private int playerID = -1;
	
	private KeyListener keyListener;
	private CharListener charListener;
	private MouseListener mouseListener;
	private MouseMotionListener mouseMotionListener;
	
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
						
						Game.net.netObjects.get(id).receiveNetUpdate(data);
						
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
