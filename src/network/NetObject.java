package network;

import java.io.Serializable;
import java.util.ArrayList;

public interface NetObject extends Serializable {

	//For Server
	public void sendNetUpdate(ArrayList<Object> data);
	//For Client
	public void receiveNetUpdate(ArrayList<Object> data);
	
	/*
	 * For rendering
	 */
	public void render();
	/*
	 * For server
	 */
	public void update(double deltaTime);
	
}
