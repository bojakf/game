package components;

import java.util.ArrayList;

import gameobject.Component;
import gameobject.Gameobject;
import main.Game;

/**
 * 
 * This class should be used for objects which can be damaged
 * 
 * @author jafi2
 *
 */
public class Damagable extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8012449035845560440L;
	
	/**
	 * The health points of the object
	 */
	protected double hp;
	
	@Override
	public void start() {
		
	}


	@Override
	public void update(double deltaTime) {
		
	}
	
	@Override
	public void render() {
		
		throw new RuntimeException("No live drawing for non players");
		
	}
	
	@Override
	protected void onDestroy() {
		
	}

	/**
	 * Damage the object
	 * @param dmg the damage to inflict
	 * @return did the object get killed/destroyed
	 */
	public final boolean damage(double dmg) {
		hp -= dmg;
		if(hp <= 0) {
			
			/*
			 * Respawning for players
			 */
			if(parent.hasComponent(Player.class)) {
			
				Player p = (Player) parent.getComponent(Player.class);
				if(p.lives == 0) destroy();
				p.lives--;
				hp = Player.INITIAL_HEALTH;
				
				ArrayList<Gameobject> spawns = Game.gameobjectsWith(Spawn.class);
				for(Gameobject g : spawns) {
					if(((Spawn)g.getComponent(Spawn.class)).playerID == p.getPlayerID()) {
						parent.pos.x = g.pos.x;
						parent.pos.y = g.pos.y;
						break;
					}
				}
				
			} else {
				destroy();
			}
			return true;
			
		}
		return false;
	}
	
	
}
