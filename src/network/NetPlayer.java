package network;

import input.MouseListener;
import input.MouseMotionListener;
import input.Keyboard.CharListener;
import input.Keyboard.KeyListener;

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
