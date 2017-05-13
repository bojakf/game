package network;

/**
 * 
 * Contains command sent during networking
 * 
 * @author jafi2
 *
 */
public class NetCommands {

	/*
	 * Server -> Client
	 */
	
	/**
	 * add a object to the client
	 */
	public static final byte ADD_OBJECT = 1;
	/**
	 * update a object
	 */
	public static final byte UPDATE_OBJECT = 2;
	/**
	 * remove a object
	 */
	public static final byte REMOVE_OBJECT = 3;
	/**
	 * disconnect
	 */
	public static final byte DISCONNECT = (byte) 0xff;
	
	/*
	 * Client -> Server
	 */
	
	/**
	 * glfw event key up
	 */
	public static final byte KEY_UP = -1;
	/**
	 * glfw event key down
	 */
	public static final byte KEY_DOWN = -2;
	/**
	 * glfw event key repeat
	 */
	public static final byte KEY_REPEAT = -3;
	/**
	 * glfw char input
	 */
	public static final byte CHAR_INPUT = -4;
	/**
	 * glfw event mouse move
	 */
	public static final byte MOUSE_MOVE = -5;
	/**
	 * glfw event mouse enter
	 */
	public static final byte MOUSE_ENTER = -6;
	/**
	 * glfw event mouse exit
	 */
	public static final byte MOUSE_EXIT = -7;
	/**
	 * glfw event mouse scroll
	 */
	public static final byte MOUSE_SCROLL = -8;
	/**
	 * glfw event mouse release
	 */
	public static final byte MOUSE_RELEASE = -9;
	/**
	 * glfw event mouse press
	 */
	public static final byte MOUSE_PRESS = -10;
	
}
