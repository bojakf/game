package map;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

import java.io.Serializable;
import java.util.ArrayList;

import input.Keyboard;
import input.Keyboard.KeyListener;
import input.Mouse;
import input.Mouse.MouseListener;
import input.Mouse.MouseMotionListener;
import loading.TexManager;
import main.Game;
import network.NetPlayer;
import physics.Collider;
import physics.Damagable;
import physics.Physics;
import physics.Ray;
import physics.RaycastHit;
import physics.Vector;

/**
 * 
 * This is the player class. It represents one player in the game. The player receives only input form the player identified by the playerID parameter
 * 
 * @author jafi2
 *
 */
public class Player extends Damagable implements NetPlayer {

	/*
	 * TODO animate player
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5203099848561064804L;

	/**
	 * The number of health points the player starts with
	 */
	private static final double INITIAL_HEALTH = 200;
	
	/**
	 * The damage the laser does per second
	 */
	private static final double LASER_DPS = 20;
	
	/**
	 * The maximum speed of the player
	 */
	private static final double PLAYER_SPEED = 3;
	
	/**
	 * The id of the player who's input is used to control the player
	 */
	private final int playerID;
	
	/**
	 * Start and end of the player's laser
	 */
	private Vector laserStart, laserEnd;
	/**
	 * Is the player's laser turned on
	 */
	private boolean laserOn = false;
	
	/**
	 * keyListener of the player
	 */
	private transient KeyListener keyListener = null;
	/**
	 * mouseListener of the player
	 */
	private transient MouseListener mouseListener = null;
	/**
	 * mouseMotionListener of the player
	 */
	private transient MouseMotionListener mouseMotionListener = null;
	
	/**
	 * Creates the player
	 * @param playerID the player who's input is used
	 */
	public Player(int playerID) {
		
		super(new Vector(10, 10), new Vector(1, 1), new Vector(), Physics.LAYER_PLAYER, INITIAL_HEALTH);
		isBlocking = true;
		
		this.playerID = playerID;
		
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
		}, playerID);
		
	}
	
	public void update(double deltaTime) {
		
		Game.playerTex.update(deltaTime);
		
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
		
		if(laserOn) {
			
			Vector dir = new Vector();
			Vector origin = new Vector();
			Vector mp = Mouse.xy(playerID);
			
			origin.x = pos.x + size.x/2;
			origin.y = pos.y + size.y/2;
			
			dir.x = -(mp.x / Game.QUAD_SIZE - origin.x);
			dir.y = -(mp.y / Game.QUAD_SIZE - origin.y);
			dir.normalize();
			
			ArrayList<Collider> ignore = new ArrayList<>();
			ignore.add(this);
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
		
		System.out.println(hp);
		
	}
	
	public void render() {
		
		glTranslated(0, 0, -0.5d);
		
		TexManager.bindTex("player");
		Game.playerTex.bindCur();
		
		if(laserOn && laserStart != null && laserEnd != null) {
			
			glColor3d(1, 1, 0);
			glDisable(GL_TEXTURE_2D);
			
			glBegin(GL_LINES);
			
			glVertex2d(laserStart.x * Game.QUAD_SIZE, laserStart.y * Game.QUAD_SIZE);
			glVertex2d(laserEnd.x * Game.QUAD_SIZE, laserEnd.y * Game.QUAD_SIZE);
			
			glEnd();
			glEnable(GL_TEXTURE_2D);
			
		}
		
		glColor3d(1, 1, 1);
		
		glTranslated(pos.x * Game.QUAD_SIZE, pos.y * Game.QUAD_SIZE, 0d);
		
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
		
		glTranslated(-pos.x * Game.QUAD_SIZE, -pos.y * Game.QUAD_SIZE, 0d);
		glTranslated(0, 0, 0.5d);
		
	}

	@Override
	public void onCollision(Collider hit) {
		
	}

	@Override
	protected void onDestroy() {
		
	}

	@Override
	public void sendNetUpdate(ArrayList<Serializable> data) {
		data.add(pos);
		data.add(size);
		data.add(velocity);
		data.add(laserStart);
		data.add(laserEnd);
		data.add(laserOn);
		data.add(hp);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Serializable> data) {
		pos = (Vector) data.get(0);
		size = (Vector) data.get(1);
		velocity = (Vector) data.get(2);
		laserStart = (Vector) data.get(3);
		laserEnd = (Vector) data.get(4);
		laserOn = (boolean) data.get(5);
		hp = (double) data.get(6);
	}

	@Override
	public int getPlayerID() {
		return playerID;
	}
	
}
