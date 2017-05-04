package player.weapon;

import static org.lwjgl.opengl.GL11.*;

import java.io.Serializable;
import java.util.ArrayList;

import input.Mouse;
import input.Mouse.MouseListener;
import main.Game;
import physics.Collider;
import physics.Damagable;
import physics.Physics;
import physics.Ray;
import physics.RaycastHit;
import physics.Vector;
import player.Player;
import player.Weapon;
import rendering.Color;

/**
 * 
 * The standard weapon of the player
 * 
 * @author jafi2
 *
 */
public class Laser implements Weapon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3873028997909399577L;

	/**
	 * The damage the laser does per second
	 */
	private static final double LASER_DPS = 20;
	
	/**
	 * The color of the laser
	 */
	private Color col;
	/**
	 * The player the weapon belongs to
	 */
	private Player player;
	
	/**
	 * Start and end of the player's laser
	 */
	private Vector laserStart, laserEnd;
	/**
	 * Is the player's laser turned on
	 */
	private boolean laserOn = false;
	
	/**
	 * mouseListener of the weapon
	 */
	private transient MouseListener mouseListener = null;
	
	/**
	 * Create a new laser
	 * @param col the color of the laser
	 * @param playerID the player the weapon belongs to
	 */
	public Laser(Color col, Player player) {
		
		this.col = col;
		this.player = player;
		
		Mouse.addMouseButtonListener(mouseListener = new MouseListener() {
			
			@Override
			public void onScroll(double delta) {}
			
			@Override
			public void onRelease(int button, int modifiers) {
				if(button == 0) laserOn = false;
			}
			
			@Override
			public void onPress(int button, int modifiers) {
				if(button == 0) {
					laserOn = true;					
				}
			}
		}, player.getPlayerID());
		
	}

	@Override
	public void update(double deltaTime) {
		
		if(laserOn) {
			
			Vector dir = new Vector();
			Vector origin = new Vector();
			Vector mp = Mouse.xy(player.getPlayerID());
			
			origin.x = player.getPosition().x + player.getSize().x/2;
			origin.y = player.getPosition().y + player.getSize().y/2;
			
			dir.x = -(mp.x / Game.QUAD_SIZE - origin.x);
			dir.y = -(mp.y / Game.QUAD_SIZE - origin.y);
			dir.normalize();
			
			ArrayList<Collider> ignore = new ArrayList<>();
			ignore.add(player);
			RaycastHit hit = Physics.raycast(new Ray(origin, dir), Physics.LAYER_DEFAULT | Physics.RAYCAST_ALL, ignore);
			
			laserStart = origin;
			if(hit != null) {
				if(hit.hit instanceof Damagable) {
					((Damagable)hit.hit).damage(LASER_DPS * deltaTime);
				}
				laserEnd = hit.pos;
			} else {
				dir.x = -dir.x;
				dir.y = -dir.y;
				dir.scale(Game.QUADS_X*Game.QUADS_X);
				dir.add(origin);
				laserEnd = dir;
			}
			
		}
		
	}

	@Override
	public void render() {
		
		if(laserOn && laserStart != null && laserEnd != null) {
			
			glTranslated(0, 0, -0.45d);
			
			col.glColor();
			glDisable(GL_TEXTURE_2D);
			
			glBegin(GL_LINES);
			
			glVertex2d(laserStart.x * Game.QUAD_SIZE, laserStart.y * Game.QUAD_SIZE);
			glVertex2d(laserEnd.x * Game.QUAD_SIZE, laserEnd.y * Game.QUAD_SIZE);
			
			glEnd();
			glEnable(GL_TEXTURE_2D);
			
			glTranslated(0, 0, 0.45d);
			
		}
		
	}

	@Override
	public boolean isPendingDestroy() {
		return false;
	}
	
	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(laserStart);
		data.add(laserEnd);
		data.add(laserOn);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		laserStart = (Vector) data.remove(0);
		laserEnd = (Vector) data.remove(0);
		laserOn = (boolean) data.remove(0);
	}

}
