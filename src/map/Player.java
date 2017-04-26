package map;

import org.lwjgl.glfw.GLFW;

import debug.Debug;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import input.Keyboard;
import input.Keyboard.CharListener;
import input.Keyboard.KeyListener;
import input.Mouse;
import input.MouseListener;
import input.MouseMotionListener;
import loading.TexManager;
import main.Game;
import main.Main;
import network.NetPlayer;
import physics.Collider;
import physics.Physics;
import physics.Ray;
import physics.RaycastHit;
import physics.Vector;

public class Player extends Collider implements NetPlayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5203099848561064804L;

	private static final double PLAYER_SPEED = 3;
	
	private int playerID;
	
	private Vector laserStart, laserEnd;
	private boolean laserOn = false;
	
	private transient KeyListener keyListener = null;
	private transient MouseListener mouseListener = null;
	private transient MouseMotionListener mouseMotionListener = null;
	
	public Player(int playerID) {
		
		super(new Vector(10, 10), new Vector(1, 1), new Vector(), Physics.LAYER_PLAYER);
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
		});
		
	}
	
	public void update(double deltaTime) {
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_W)) {
			velocity.y = 2;
		} else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_S)) {
			velocity.y = -2;
		} else {
			velocity.y = 0;
		}		
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D)) {
			velocity.x = 2;
		} else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
			velocity.x = -2;
		} else {
			velocity.x = 0;
		}
		
		if(laserOn) {
			
			Vector dir = new Vector();
			Vector origin = new Vector();
			
			origin.x = Player.this.pos.x + Player.this.size.x/2;
			origin.y = Player.this.pos.y + Player.this.size.y/2;
			
			dir.x = -(Mouse.x() / Game.QUAD_SIZE - origin.x);
			dir.y = -(Mouse.y() / Game.QUAD_SIZE - origin.y);
			dir.normalize();
			
			RaycastHit hit = Physics.raycast(new Ray(origin, dir), Physics.LAYER_PLAYER);
			
			laserStart = origin;
			if(hit != null) {
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
	
	public void render() {
		
		glTranslated(0, 0, -0.5d);
		
		TexManager.bindTex("player");
		
		if(laserOn && laserStart != null && laserEnd != null) {
			
			glColor3d(1, 1, 0);
			
			glBegin(GL_LINES);
			
			glVertex2d(laserStart.x * Game.QUAD_SIZE, laserStart.y * Game.QUAD_SIZE);
			glVertex2d(laserEnd.x * Game.QUAD_SIZE, laserEnd.y * Game.QUAD_SIZE);
			
			glEnd();
			
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
	@Deprecated
	public Object[] getHitInfo() {
		return null;
	}

	@Override
	public void sendNetUpdate(ArrayList<Object> data) {
		data.add(pos);
		data.add(size);
		data.add(velocity);
		data.add(laserStart);
		data.add(laserEnd);
		data.add(laserOn);
	}

	@Override
	public void receiveNetUpdate(ArrayList<Object> data) {
		pos = (Vector) data.get(0);
		size = (Vector) data.get(1);
		velocity = (Vector) data.get(2);
		laserStart = (Vector) data.get(3);
		laserEnd = (Vector) data.get(4);
		laserOn = (boolean) data.get(5);
	}

	@Override
	public KeyListener getKeyListener() {
		return keyListener;
	}
	
	@Override
	public CharListener getCharListener() {
		return null;
	}

	@Override
	public MouseListener getMouseListener() {
		return mouseListener;
	}
	
	@Override
	public MouseMotionListener getMouseMotionListener() {
		return mouseMotionListener;
	}

	@Override
	public int getPlayerID() {
		return playerID;
	}
	
}
