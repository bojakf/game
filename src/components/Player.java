package components;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.io.Serializable;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import gameobject.Gameobject;
import input.Keyboard;
import input.Mouse;
import input.Mouse.MouseListener;
import main.Game;
import physics.Vector;
import rendering.Color;
import rendering.StringDrawer;
import weapon.GrenadeLauncher;
import weapon.Laser;
import weapon.RocketLauncher;
import weapon.Weapon;

/**
 * 
 * Component for a player
 * 
 * @author jafi2
 *
 */
public class Player extends Damageable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5472313103719282017L;

	/**
	 * The number of health points the player starts with
	 */
	static final double INITIAL_HEALTH = 200;
	
	/**
	 * The maximum speed of the player
	 */
	private static final double PLAYER_SPEED = 5;
	
	/**
	 * The lives the player has after joining the game
	 */
	private static final int INITIAL_PLAYER_LIVES = 9;
	
	/**
	 * The id of the player who's input is used to control the player
	 */
	private int playerID = -1;
	
	/**
	 * The lives the player has left
	 */
	int lives = INITIAL_PLAYER_LIVES;
	
	/**
	 * The list of Weapons for the player
	 */
	private ArrayList<Weapon> weapons;
	
	/**
	 * The index of the currently selected weapon
	 */
	private int selectedWeapon = 0;
	
	/**
	 * Listener for scrolling
	 */
	private transient MouseListener mouseListener;
	
	/**
	 * Create the default player component
	 */
	public Player() {
		super(INITIAL_HEALTH);
	}
	
	@Override
	public void start() {
		
		Mouse.addMouseButtonListener(mouseListener = new MouseListener() {
			
			@Override
			public void onScroll(double delta) {
				selectedWeapon -= (int)delta;
				while(selectedWeapon < 0) {
					selectedWeapon = weapons.size() + selectedWeapon;
				}
				while(selectedWeapon >= weapons.size()) {
					selectedWeapon -= weapons.size();
				}
			}
			
			@Override
			public void onRelease(int button, int modifiers) {
				
			}
			
			@Override
			public void onPress(int button, int modifiers) {
				
			}
		}, playerID);
		
		Color wCol = null;
		if(playerID == 0) {
			wCol = new Color(1, 0, 0);
		} else if(playerID == 1) {
			wCol = new Color(0, 1, 0);
		} else if(playerID == 2) {
			wCol = new Color(0, 0, 1);
		} else if(playerID == 3) {
			wCol = new Color(1, 1, 0);
		}
		
		weapons = new ArrayList<>();
		
		weapons.add(new Laser(wCol, this));
		weapons.get(0).texName = "laser";
		weapons.add(new RocketLauncher(this));
		weapons.get(1).texName = "rocketLauncher";
		weapons.add(new GrenadeLauncher(this));
		weapons.get(2).texName = "grenadeLauncher";
		
		/*
		 * Move player to spawnpoint
		 */
		ArrayList<Gameobject> spawns = Game.gameobjectsWith(Spawn.class);
		for(Gameobject g : spawns) {
			if(((Spawn)g.getComponent(Spawn.class)).playerID == playerID) {
				parent.pos.x = g.pos.x;
				parent.pos.y = g.pos.y;
				break;
			}
		}
		

		super.start();
		
	}

	@Override
	public void update(double deltaTime) {
		
		weapons.get(selectedWeapon).update(deltaTime);
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_W, playerID)) {
			parent.velocity.y = 1;
		} else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_S, playerID)) {
			parent.velocity.y = -1;
		} else {
			parent.velocity.y = 0;
		}		
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D, playerID)) {
			parent.velocity.x = 1;
		} else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A, playerID)) {
			parent.velocity.x = -1;
		} else {
			parent.velocity.x = 0;
		}
		parent.velocity.normalize();
		parent.velocity.scale(PLAYER_SPEED);
		
	}

	@Override
	public void render() {
		
		weapons.get(selectedWeapon).render();
		
		glTranslated((parent.pos.x-parent.size.x*0.5) * Game.QUAD_SIZE, parent.pos.y * Game.QUAD_SIZE + parent.size.y*0.5*Game.QUAD_SIZE, 0);
		
		if(playerID == 0) {
			glColor3d(1, 0, 0);
		} else if(playerID == 1) {
			glColor3d(0, 1, 0);
		} else if(playerID == 2) {
			glColor3d(0, 0, 1);
		} else if(playerID == 3) {
			glColor3d(1, 1, 0);
		}
		
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		
		glVertex2d(0, 15);
		glVertex2d(parent.size.x * hp/Player.INITIAL_HEALTH * parent.size.x * Game.QUAD_SIZE, 15);
		glVertex2d(parent.size.x * hp/Player.INITIAL_HEALTH * parent.size.x * Game.QUAD_SIZE, 0);
		glVertex2d(0, 0);
		
		glEnd();
		glEnable(GL_TEXTURE_2D);
		
		StringDrawer.setFontSize(20, 20);
		StringDrawer.drawStringCentered("" + lives, -12, 7.5f);
		StringDrawer.resetFontSize();
		
		glTranslated(-(parent.pos.x-parent.size.x*0.5) * Game.QUAD_SIZE, -parent.pos.y * Game.QUAD_SIZE - parent.size.y*0.5*Game.QUAD_SIZE, 0);
		
	}

	@Override
	protected void onDestroy() {
		if(mouseListener != null) Mouse.removeMouseButtonListener(mouseListener, playerID);
	}
	
	/**
	 * Set the id of the player. Must only be called once with an id which is greater or equal to 0
	 * @param playerID the new playerID
	 */
	public void setPlayerID(int playerID) {
		if(this.playerID != -1) {
			throw new RuntimeException("Player id already set");
		}
		if(playerID < 0) {
			throw new RuntimeException("Invalid player id");
		}
		this.playerID = playerID;
	}
	
	/**
	 * get the id of the player
	 * @return the id of the player
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/**
	 * Set the position of the player
	 * @param pos the new position
	 */
	public void setPosition(Vector pos) {
		parent.pos.x = pos.x;
		parent.pos.y = pos.y;
	}
	
	/**
	 * Get the position of the player
	 * @return the position of the player
	 */
	public Vector getPosition() {
		return parent.pos;
	}
	
	/**
	 * Get the size of the players collider
	 * @return the size
	 */
	public Vector getSize() {
		return parent.size;
	}
	
	/**
	 * Returns the index of the selected weapon
	 * @return
	 */
	public int getSelectedWeapon() {
		return selectedWeapon;
	}
	
	/**
	 * Get the weapon with the specified index
	 * @param index the index of the weapon
	 * @return the weapon
	 */
	public Weapon getWeapon(int index) {
		return weapons.get(index);
	}
	
	/**
	 * Get number of weapons the player has
	 * @return the number of weapons
	 */
	public int getWeaponCount() {
		return weapons.size();
	}
	

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		super.sendNetUpdate(data);
		data.add(selectedWeapon);
		weapons.get(selectedWeapon).sendNetUpdate(data);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		super.receiveNetUpdate(data);
		selectedWeapon = (int) data.remove(0);
		weapons.get(selectedWeapon).receiveNetUpdate(data);
	}

}
