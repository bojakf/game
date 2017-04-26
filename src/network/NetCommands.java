package network;

public class NetCommands {

	/*
	 * Server -> Client
	 */
	public static final byte ADD_OBJECT = 1;
	public static final byte UPDATE_OBJECT = 2;
	public static final byte ADD_PLAYER = 3;
	public static final byte DISCONNECT = (byte) 0xff;
	
	/*
	 * Client -> Server
	 */
	public static final byte KEY_UP = -1;
	public static final byte KEY_DOWN = -2;
	public static final byte KEY_REPEAT = -3;
	public static final byte CHAR_INPUT = -4;
	public static final byte MOUSE_MOVE = -5;
	public static final byte MOUSE_ENTER = -6;
	public static final byte MOUSE_EXIT = -7;
	public static final byte MOUSE_SCROLL = -8;
	public static final byte MOUSE_RELEASE = -9;
	public static final byte MOUSE_PRESS = -10;
	
}
