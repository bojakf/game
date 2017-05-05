package rendering.effects;

import java.io.Serializable;
import java.util.ArrayList;

import main.Game;
import network.NetObject;
import physics.Vector;

/**
 * 
 * Super class for effects. 
 * Effects only work while in-game
 * 
 * @author jafi2
 *
 */
public abstract class Effect implements NetObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3072906034179668427L;
	
	/**
	 * Time until the effect ends
	 */
	protected double ttl;
	/**
	 * The position of the effect
	 */
	protected Vector pos;
	/**
	 * The size of the effect
	 */
	protected Vector size;
	
	/**
	 * Creates a new effect
	 * @param pos position of the effect
	 * @param size size of the effect
	 * @param ttl time until the effect ends
	 */
	public Effect(Vector pos, Vector size, double ttl) {
		this.pos = pos;
		this.size = size;
		this.ttl = ttl;
		
		Game.net.registerNetObject(this);
	}
	
	@Override
	public void update(double deltaTime) {
		ttl-=deltaTime;
	}
	
	@Override
	public boolean isPendingDestroy() {
		return ttl <= 0;
	}
	
	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(pos);
		data.add(size);
		data.add(ttl);
	}
	
	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		pos = (Vector) data.remove(0);
		size = (Vector) data.remove(0);
		ttl = (double) data.remove(0);
	}
	
}
