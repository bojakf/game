package components;

import java.io.Serializable;
import java.util.ArrayList;

import gameobject.Component;
import main.Game;

/**
 * 
 * This component is for gameobjects which should be sent to server clients.
 * The parent of this component and all the parent's components are sent to the clients
 * sendNetUpdate and reveiveNetUpdate handle updates of the netComponent
 * They should be primarily used for rendered components
 * 
 * @author jafi2
 *
 */
public abstract class NetComponent extends Component {

	/*
	 * TODO add final netComponents (map)
	 */
	/*
	 * TODO only send this component to the client
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6647214981735886936L;

	/**
	 * Does nothing at the moment
	 */
	public NetComponent() {
		
	}
	
	@Override
	public void start() {
		
		if(Game.net == null) {
			try {
				throw new RuntimeException("No network found");
			} catch(RuntimeException e) {
				e.printStackTrace();
				return;
			}
		}
		
		Game.net.add(this);
	
	}
	
	/**
	 * Used by the server to gather the information to send to the client during client update
	 * @param data add information to this object<br>
	 * <b>Note: </b>the object is already created at the start of the method execution
	 */
	public abstract void sendNetUpdate(ArrayList<Serializable> data);
	/**
	 * Used by the clients to update the information of the object
	 * @param data update data
	 */
	public abstract void receiveNetUpdate(ArrayList<Serializable> data);
	
}
