package components;

import loading.AnimatedTexture;

/**
 * 
 * Super class for effects. 
 * Effects only work while in-game
 * 
 * @author jafi2
 *
 */
public class Effect extends Renderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3072906034179668427L;
	
	/**
	 * Time until the effect ends (time to live)
	 */
	protected double ttl;
	
	/**
	 * Creates a new effect
	 * @param texName the texture of the effect
	 * @param ttl time until the effect ends
	 */
	public Effect(String texName, Double ttl) {
		super(texName);
		this.ttl = ttl;
	}
	
	/**
	 * Creates a new effect
	 * @param tex the animation of the effect
	 * @param ttl time until the effect ends
	 */
	public Effect(AnimatedTexture tex, Double ttl) {
		super(tex);
		this.ttl = ttl;
	}
	
	/**
	 * Creates a new effect using the animation duration as time to live
	 * @param tex the animation of the effect
	 */
	public Effect(AnimatedTexture tex) {
		super(tex);
		this.ttl = tex.getDuration();
	}
	
	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		ttl-=deltaTime;
		if(ttl < 0) {
			parent.destroy();
		}
	}
	
}
