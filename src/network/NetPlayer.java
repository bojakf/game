package network;

import input.Keyboard.CharListener;
import input.Keyboard.KeyListener;
import input.Mouse.MouseListener;
import input.Mouse.MouseMotionListener;

public interface NetPlayer extends NetObject {
	
	/*
	 * For server
	 */
	public KeyListener getKeyListener();
	public CharListener getCharListener();
	public MouseListener getMouseListener();
	public MouseMotionListener getMouseMotionListener();
	public int getPlayerID();
	
}
