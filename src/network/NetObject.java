package network;

import java.io.Serializable;
import java.util.ArrayList;

public interface NetObject extends Serializable {

	/**
	 * Used by the server to gather the information to send to the client during client update
	 * @param data add information to this object<br>
	 * <b>Note: </b>the object is already created at the start of the method execution
	 */
	public void sendNetUpdate(ArrayList<Serializable> data);
	/**
	 * Used by the clients to update the information of the object
	 * @param data update data
	 */
	public void receiveNetUpdate(ArrayList<Serializable> data);
	
	/**
	 * render the object. Should be called once per frame
	 */
	public void render();
	/**
	 * Update the object. Should be called once per frame only server-side
	 * @param deltaTime the time since the last update
	 */
	public void update(double deltaTime);
	
}
