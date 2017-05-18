package mapCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import gameobject.Gameobject;
import gameobject.Primitive;
import input.Keyboard;
import levels.Level;
import main.Game;
import main.Main;
import map.CameraController;
import physics.Physics;
import physics.Vector;
import ui.Button;
import ui.Button.ClickListener;
import ui.Ui;

public class MapCreator extends Level {
	
	private static final double CAM_SPEED = 15;
	
	private Ui ui;
	
	private static ArrayList<Gameobject> newGameobjects;
	
	public static boolean showColliders = false;
	
	public static final ArrayList<Primitive> mapObjects = new ArrayList<>();
	
	public MapCreator() {
		
		newGameobjects = new ArrayList<>();
		
		CameraController.active = false;
		Game.camX = 0;
		Game.camY = 0;
		
		ui = new Ui();
		new MapSettingsUI();
		
		for(int i = 0; i < mapObjects.size(); i++) {
			if(i == 0) {
				Gameobject g = mapObjects.get(0).create(new Vector(0.6, 0.6));
				g.addComponent(new MapCreatorSelected());
				newGameobjects.add(g);
			} else {
				if(i%2 == 0) {
					newGameobjects.add(mapObjects.get(i).create(new Vector(Game.camX+0.6, Game.camY+0.6+(i/2)*1.1)));
				} else {
					newGameobjects.add(mapObjects.get(i).create(new Vector(Game.camX+1.7, Game.camY+0.6+(i/2)*1.1)));
				}
			}
		}		
		
	}
	
	@Override
	public void update(double deltaTime) {
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_W, Keyboard.LOCAL)) {
			Game.camY+=CAM_SPEED*deltaTime;
		}
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A, Keyboard.LOCAL)) {
			Game.camX-=CAM_SPEED*deltaTime;
		}
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_S, Keyboard.LOCAL)) {
			Game.camY-=CAM_SPEED*deltaTime;
		}
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D, Keyboard.LOCAL)) {
			Game.camX+=CAM_SPEED*deltaTime;
		}
		
		for(int i = 0; i < newGameobjects.size(); i++) {
			if(!newGameobjects.get(i).hasComponent(MapCreatorSelected.class)) {
				if(i%2==0) {
					newGameobjects.get(i).pos.set(new Vector(Game.camX+0.6, Game.camY+0.6+(i/2)*1.1));
				} else {
					newGameobjects.get(i).pos.set(new Vector(Game.camX+1.7, Game.camY+0.6+(i/2)*1.1));
				}
			}
			newGameobjects.get(i).update(deltaTime);
		}
		
		ui.update(deltaTime);
	}

	@Override
	public void render() {
		
		GL11.glTranslated(-Game.camX*Game.QUAD_SIZE+Game.WORLD_OFFSET_X*Game.QUAD_SIZE, -Game.camY*Game.QUAD_SIZE+Game.WORLD_OFFSET_X*Game.QUAD_SIZE, 0);
		for(int i = 0; i < newGameobjects.size(); i++) {
			newGameobjects.get(i).render();
		}
		if(showColliders) {
			Physics.drawColliders();
		}
		GL11.glTranslated(Game.camX*Game.QUAD_SIZE-Game.WORLD_OFFSET_X*Game.QUAD_SIZE, Game.camY*Game.QUAD_SIZE-Game.WORLD_OFFSET_X*Game.QUAD_SIZE, 0);
		
		ui.render();
		
	}

	@Override
	public void onClose() {
		ui.render();
		newGameobjects = null;
	}
	
	protected static void save(String name) throws IOException {
		
		File file = new File(Game.gamePath + "maps\\");
		file.mkdirs();
		FileOutputStream fos = new FileOutputStream(new File(file.getPath() + "\\" + name + ".sav"));
		ObjectOutputStream out = new ObjectOutputStream(fos);
		
		Gameobject[] gameobjects = Game.getGameobjects();
		out.writeInt(gameobjects.length);		
		for(Gameobject g : gameobjects) {
			g.removeComponent(MapCreatorSelected.class);
			out.writeObject(g);
		}
		
		out.flush();
		out.close();
		
	}

	private static void addNewObject(int index) {
		if(index%2 == 0) {
			newGameobjects.set(index, mapObjects.get(index).create(new Vector(Game.camX+0.6, Game.camY+0.6+(index/2)*1.1)));
		} else {
			newGameobjects.set(index, mapObjects.get(index).create(new Vector(Game.camX+1.7, Game.camY+0.6+(index/2)*1.1)));
		}
	}
	
	public static Gameobject[] getNewObjects() {
		Gameobject[] r = new Gameobject[newGameobjects.size()];
		for(int i = 0; i < r.length; i++) {
			r[i] = newGameobjects.get(i);
		}
		return r;
	}
	
	public static void removeNewObject(Gameobject r) {
		int index = newGameobjects.indexOf(r);
		addNewObject(index);
	}
	
	public static boolean isNewObject(Gameobject n) {
		return newGameobjects.contains(n);
	}
	
}
