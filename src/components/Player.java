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
import main.Game;
import physics.Vector;
import player.Weapon;
import player.weapon.GrenadeLauncher;
import player.weapon.Laser;
import rendering.Color;
import rendering.StringDrawer;

/**
 * 
 * Component for a player
 * 
 * @author jafi2
 *
 */
public class Player extends Damagable {

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
	 * The weapon the player is currently using
	 */
	private Weapon curWeapon;
	
	@Override
	public void start() {
		
		hp = INITIAL_HEALTH;
		
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
		
		if(playerID == 0) {
			curWeapon = new GrenadeLauncher(this);
		} else {
			curWeapon = new Laser(wCol, this);
		}
		
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
		
		curWeapon.update(deltaTime);
		
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
		
		curWeapon.render();
		
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

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		super.sendNetUpdate(data);
		curWeapon.sendNetUpdate(data);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		super.receiveNetUpdate(data);
		curWeapon.receiveNetUpdate(data);
	}

}
