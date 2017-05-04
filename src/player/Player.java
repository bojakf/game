package player;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

import java.io.Serializable;
import java.util.ArrayList;

import input.Keyboard;
import input.Keyboard.KeyListener;
import input.Mouse;
import input.Mouse.MouseListener;
import input.Mouse.MouseMotionListener;
import main.Game;
import map.PlayerSpawn;
import network.NetPlayer;
import physics.Collider;
import physics.Damagable;
import physics.Physics;
import physics.Ray;
import physics.RaycastHit;
import physics.Vector;
import player.weapon.GrenadeLauncher;
import player.weapon.Laser;
import rendering.AnimatedTexture;
import rendering.Color;
import rendering.StringDrawer;

/**
 * 
 * This is the player class. It represents one player in the game. The player receives only input form the player identified by the playerID parameter
 * 
 * @author jafi2
 *
 */
public class Player extends Damagable implements NetPlayer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5203099848561064804L;

	/**
	 * The number of health points the player starts with
	 */
	private static final double INITIAL_HEALTH = 200;
	
	/**
	 * The maximum speed of the player
	 */
	private static final double PLAYER_SPEED = 3;
	
	/**
	 * The lives the player has after joining the game
	 */
	private static final int INITIAL_PLAYER_LIVES = 9;
	
	/**
	 * The id of the player who's input is used to control the player
	 */
	private final int playerID;
	
	/**
	 * The spawn of the player
	 */
	public PlayerSpawn spawn;
	/**
	 * The lives the player has left
	 */
	private int lives = INITIAL_PLAYER_LIVES;
	/**
	 * The weapon the player is currently using
	 */
	private Weapon curWeapon;
	
	/**
	 * The animation for the player
	 */
	private AnimatedTexture tex = Game.playerTex.clone();
	
	/**
	 * Creates the player
	 * @param playerID the player who's input is used
	 */
	public Player(int playerID) {
		
		super(new Vector(10, 10), new Vector(1, 1), new Vector(), Physics.LAYER_PLAYER, INITIAL_HEALTH);
		isBlocking = true;
		
		this.playerID = playerID;
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
		
		
	}
	
	public void update(double deltaTime) {
		
		curWeapon.update(deltaTime);
		
		tex.update(deltaTime);
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_W, playerID)) {
			velocity.y = 1;
		} else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_S, playerID)) {
			velocity.y = -1;
		} else {
			velocity.y = 0;
		}		
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D, playerID)) {
			velocity.x = 1;
		} else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A, playerID)) {
			velocity.x = -1;
		} else {
			velocity.x = 0;
		}
		velocity.normalize();
		velocity.scale(PLAYER_SPEED);
		
	}
	
	public void render() {
		
		curWeapon.render();
		
		tex.bindCur();
		glColor3d(1, 1, 1);
		
		glTranslated(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE, -0.5d);
		
		glBegin(GL_QUADS);
		
		glTexCoord2d(0, 0);
		glVertex2d(0, size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 0);
		glVertex2d(size.x * Game.QUAD_SIZE, size.y * Game.QUAD_SIZE);
		
		glTexCoord2d(1, 1);
		glVertex2d(size.x * Game.QUAD_SIZE, 0);
		
		glTexCoord2d(0, 1);
		glVertex2d(0, 0);
		
		glEnd();
		
		glTranslated(-pos.x * Game.QUAD_SIZE, -pos.y * Game.QUAD_SIZE, 0.5d);
		
		/*
		 * 
		 * Draw lives
		 * 
		 */
		
		glTranslated(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE + size.y * Game.QUAD_SIZE, -10);
		
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
		glVertex2d(size.x * hp/INITIAL_HEALTH * size.x * Game.QUAD_SIZE, 15);
		glVertex2d(size.x * hp/INITIAL_HEALTH * size.x * Game.QUAD_SIZE, 0);
		glVertex2d(0, 0);
		
		glEnd();
		glEnable(GL_TEXTURE_2D);
		
		StringDrawer.drawStringCentered("" + lives, -20, 7.5f);
		
		glTranslated(-pos.x * Game.QUAD_SIZE, -pos.y * Game.QUAD_SIZE - size.y * Game.QUAD_SIZE, 10);
		
	}
	
	/**
	 * Set the position of the player
	 * @param pos the new position
	 */
	public void setPosition(Vector pos) {
		this.pos = pos;
	}
	
	/**
	 * Get the position of the player
	 * @return the position of the player
	 */
	public Vector getPosition() {
		return pos;
	}
	
	/**
	 * Get the size of the players collider
	 * @return the size
	 */
	public Vector getSize() {
		return size;
	}

	@Override
	public void onCollision(Collider hit) {
		
	}

	@Override
	protected void onDestroy() {
		if(Game.net.isServer()) {
			if(lives == 0) return;
			lives--;
			pendingDestroy = false;
			Physics.registerCollider(this);
			hp = INITIAL_HEALTH;
			pos = spawn.getPosition();
		}
	}

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(pos);
		data.add(size);
		data.add(velocity);
		curWeapon.sendNetUpdate(data);
		data.add(hp);
		data.add(tex.curTex);
		data.add(lives);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		pos = (Vector) data.remove(0);
		size = (Vector) data.remove(0);
		velocity = (Vector) data.remove(0);
		curWeapon.receiveNetUpdate(data);
		hp = (double) data.remove(0);
		tex.curTex = (int) data.remove(0);
		lives = (int) data.remove(0);
	}

	@Override
	public int getPlayerID() {
		return playerID;
	}
	
}
