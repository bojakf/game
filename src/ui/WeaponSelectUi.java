package ui;

import static org.lwjgl.opengl.GL11.*;

import components.Player;
import gameobject.Gameobject;
import loading.TexManager;
import main.Game;
import main.Main;
import physics.Vector;

public class WeaponSelectUi extends UiObject {

	private Player player;
	private int playerID;
	private static final double SHOW_TIME = 5;
	private double lastChange = SHOW_TIME+1;
	private int lastSelected = 0;
	
	public WeaponSelectUi(Ui ui, int playerID) {
		super(ui);
		this.playerID = playerID;
	}

	@Override
	protected void update(double deltaTime) {
		
		if(player == null) {
			for(Gameobject g : Game.getGameobjects()) {
				if(g.hasComponent(Player.class) && ((Player)g.getComponent(Player.class)).getPlayerID() == Game.net.playerID) {
					player = (Player)g.getComponent(Player.class);
					break;
				}
			}
			if(player == null) return;
		}
		
		
		lastChange += deltaTime;
		if(lastSelected != player.getSelectedWeapon()) {
			lastSelected = player.getSelectedWeapon();
			lastChange = 0;
		}
		
	}
	
	@Override
	protected void render() {
		
		if(player == null) return;
		
		if(lastChange < SHOW_TIME) {
			
			Vector pos = new Vector(Main.windowWidth/2d, 10);
			Vector size = new Vector(80, 80);
			
			glColor4d(0, 0, 0, 0.8);
			glDisable(GL_TEXTURE_2D);
			glBegin(GL_QUADS);
			
			
			
			glVertex2d(pos.x, pos.y + size.y);
			glVertex2d(pos.x + size.x, pos.y + size.y);
			glVertex2d(pos.x + size.x, pos.y);
			glVertex2d(pos.x, pos.y);
			
			glEnd();
			
			if(player.getWeapon(lastSelected).texName != null) {
				
				glEnable(GL_TEXTURE_2D);
				
				TexManager.bindTex(player.getWeapon(lastSelected).texName);
				glColor3d(1, 1, 1);
				glBegin(GL_QUADS);
				
				glTexCoord2d(0, 0);
				glVertex2d(pos.x, pos.y + size.y);
				
				glTexCoord2d(1, 0);
				glVertex2d(pos.x + size.x, pos.y + size.y);
				
				glTexCoord2d(1, 1);
				glVertex2d(pos.x + size.x, pos.y);
				
				glTexCoord2d(0, 1);
				glVertex2d(pos.x, pos.y);
				
				glEnd();
				glDisable(GL_TEXTURE_2D);
				
			}
			
			size.x = 60;
			size.y = 60;
			pos.x -= 70*(lastSelected+1);
			for(int i = 0; i < player.getWeaponCount(); i++) {
				if(i == lastSelected) {
					pos.x += 90;
					continue;
				}
				pos.x += 70;
				
				glColor4d(0, 0, 0, 0.8);
				glBegin(GL_QUADS);
				
				glVertex2d(pos.x, pos.y + size.y);
				glVertex2d(pos.x + size.x, pos.y + size.y);
				glVertex2d(pos.x + size.x, pos.y);
				glVertex2d(pos.x, pos.y);
				
				glEnd();
				
				if(player.getWeapon(i).texName != null) {
					
					glEnable(GL_TEXTURE_2D);
					TexManager.bindTex(player.getWeapon(i).texName);
					glColor3d(1, 1, 1);
					glBegin(GL_QUADS);
					
					glTexCoord2d(0, 0);
					glVertex2d(pos.x, pos.y + size.y);
					
					glTexCoord2d(1, 0);
					glVertex2d(pos.x + size.x, pos.y + size.y);
					
					glTexCoord2d(1, 1);
					glVertex2d(pos.x + size.x, pos.y);
					
					glTexCoord2d(0, 1);
					glVertex2d(pos.x, pos.y);
					
					glEnd();
					glDisable(GL_TEXTURE_2D);
					
				}
			}
			
			glEnable(GL_TEXTURE_2D);
			
		}
		
	}
	
	@Override
	protected void onDestroy() {
		
	}
	
}
