package components;

import java.io.Serializable;
import java.util.ArrayList;

import gameobject.Component;
import physics.Collider;

public class HealthPack extends Component {

	private double hpGain = 50;
	private boolean available = true;
	private double reloadTime = 5;
	private double time = 0;
	private Renderer renderer;
	private String tex;
	
	@Override
	public void start() {
		renderer = (Renderer) parent.getComponent(Renderer.class);
		tex = renderer.getTex();
	}
	
	@Override
	public void update(double deltaTime) {
		if(time > reloadTime) {
			time = 0;
			available = true;
			renderer.setTex(tex);
		}
		if(!available) {
			time += deltaTime;
		}
	}

	@Override
	public void render() {}
	
	@Override
	protected void onDestroy() {}
	
	@Override
	public void onCollision(Collider hit) {
		if(hit.getParent().hasComponent(Player.class)) {
			useHealthPack((Player)hit.getParent().getComponent(Player.class));
		}
	}
	
	public boolean useHealthPack(Player p) {
		if(!available) return false;
		if(p.hp >= Player.INITIAL_HEALTH) return false;
		p.hp = Math.min(p.hp+hpGain, Player.INITIAL_HEALTH);
		available = false;
		renderer.setTex("reload");
		return true;
	}

}
