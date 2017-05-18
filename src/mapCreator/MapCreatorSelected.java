package mapCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import components.FinalRenderer;
import components.Renderer;
import gameobject.Component;
import gameobject.Gameobject;
import input.Keyboard;
import input.Mouse;
import main.Game;
import physics.Vector;
import rendering.Color;

public class MapCreatorSelected extends Component {

	private static final ArrayList<MapCreatorSelected> selected = new ArrayList<>();

	private static boolean duplicateDown = false;
	
	public static boolean clipToGrid = true;
	
	private boolean deletePressed = true;
	
	private Vector lm = Mouse.xy(Mouse.LOCAL);
	
	private Vector startPos;
	
	@Override
	public void start() {
		
		selected.add(this);
		
		if(parent.hasComponent(Renderer.class)) {
			Renderer r = ((Renderer) parent.getComponent(Renderer.class));
			r.col = new Color(r.col.r*0.8, r.col.g*0.8, r.col.b*0.8, r.col.a);
		} else if(parent.hasComponent(FinalRenderer.class)) {
			FinalRenderer r = ((FinalRenderer) parent.getComponent(FinalRenderer.class));
			r.col = new Color(r.col.r*0.8, r.col.g*0.8, r.col.b*0.8, r.col.a);
		}
		
		startPos = parent.pos.clone();
		
	}

	@Override
	public void update(double deltaTime) {
		
		if(isDestroyed()) return;
		
		/*
		 * Deleting
		 */
		if(!deletePressed && Keyboard.isKeyDown(GLFW.GLFW_KEY_DELETE, Keyboard.LOCAL)) {
			
			Gameobject[] gameobjects = Game.getGameobjects();
			for(Gameobject g : gameobjects) {
				if(g == null) continue;
				if(!g.hasComponent(MapCreatorSelected.class)) {
					g.addComponent(new MapCreatorSelected());
					parent.destroy();
					return;
				}
			}
			gameobjects = MapCreator.getNewObjects();
			for(Gameobject g : gameobjects) {
				if(g == null) continue;
				if(!g.hasComponent(MapCreatorSelected.class)) {
					g.addComponent(new MapCreatorSelected());
						parent.destroy();
					return;
				}
			}
			
		} else if(!Keyboard.isKeyDown(GLFW.GLFW_KEY_DELETE, Keyboard.LOCAL)) {
			deletePressed = false;
		}
		
		Vector m = Mouse.xy(Mouse.LOCAL);
		m.x /= Game.QUAD_SIZE;
		m.y /= Game.QUAD_SIZE;
		m.x += Game.camX-Game.WORLD_OFFSET_X;
		m.y += Game.camY-Game.WORLD_OFFSET_Y;
		
		/*
		 * Duplicating
		 */
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_Q, Keyboard.LOCAL)) {
			
			if(!duplicateDown) {
				
				try {
					
					File tmpFile = File.createTempFile("Object_Copy", ".tmp");
					if(!tmpFile.exists()) throw new RuntimeException("Could not create tmp file");
					
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpFile));
					
					out.writeInt(selected.size());
					while (selected.size() > 0) {
						MapCreatorSelected s = selected.get(0);
						s.parent.removeComponent(MapCreatorSelected.class);
						out.writeObject(s.parent);
					}
					selected.clear();
					out.close();
					
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpFile));
					
					int c = in.readInt();
					double x1 = 0, y1 = 0;
					for(int i = 0; i < c; i++) {
						Gameobject g = (Gameobject)in.readObject();
						if(i == 0) {
							x1 = g.pos.x;
							y1 = g.pos.y;
							if(clipToGrid) {
								g.pos.x = Math.round(m.x);
								g.pos.y = Math.round(m.y);
							} else {
								g.pos.x = m.x;
								g.pos.y = m.y;
							}
						} else {
							if(clipToGrid) {
								g.pos.x = g.pos.x - x1 + Math.round(m.x);
								g.pos.y = g.pos.y - y1 + Math.round(m.y);
							} else {
								g.pos.x = g.pos.x - x1 + m.x;
								g.pos.y = g.pos.y - y1 + m.y;
							}
						}
						g.addComponent(new MapCreatorSelected());
						g.init();
					}
					
					in.close();
					
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			
			}
			duplicateDown = true;
			
		} else {
			duplicateDown = false;
		}
		
		/*
		 * Moving
		 */
		
		if(Mouse.isMouseButtonDown(0, Mouse.LOCAL)) {
			
			if(checkCursorInside(lm, parent.pos, parent.size)) {
				if(clipToGrid) {
					for(MapCreatorSelected s : selected) {
						if(s == this) continue;
						s.parent.pos.x += Math.round(m.x) - parent.pos.x;
						s.parent.pos.y += Math.round(m.y) - parent.pos.y;
					}
					parent.pos.x = Math.round(m.x);
					parent.pos.y = Math.round(m.y);
				} else {
					for(MapCreatorSelected s : selected) {
						if(s == this) continue;
						s.parent.pos.x += m.x - parent.pos.x;
						s.parent.pos.y += m.y - parent.pos.y;
					}
					parent.pos.x = m.x;
					parent.pos.y = m.y;
				}
			} else {
				
				/*
				 * check if cursor is inside other selected
				 */
				boolean over = false;
				for(MapCreatorSelected s : selected) {
					if(checkCursorInside(m, s.getParent().pos, s.getParent().size)) {
						over = true;
						break;
					}
				}
			
				if(!over) {
					
					Gameobject[] gameobjects = Game.getGameobjects();
					for(Gameobject g : gameobjects) {
						if(g == null || g == this.parent) continue;
						if(checkCursorInside(m, g.pos, g.size) && !g.hasComponent(MapCreatorSelected.class)) {
							g.addComponent(new MapCreatorSelected());
							if(!Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL, Keyboard.LOCAL))
								while(selected.size() > 0) selected.get(0).destroy();
							break;
						}
					}
					if(!isDestroyed()) {
						gameobjects = MapCreator.getNewObjects();
						for(Gameobject g : gameobjects) {
							if(g == null || g == this.parent) continue;
							if(checkCursorInside(m, g.pos, g.size)
									 && !g.hasComponent(MapCreatorSelected.class)) {
								g.addComponent(new MapCreatorSelected());
								if(!Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL, Keyboard.LOCAL))
									while(selected.size() > 0) selected.get(0).destroy();
								break;
							}
						}
					}
				
				}
				
			}
			
		} else {
			if(MapCreator.isNewObject(parent)) {
				MapCreator.removeNewObject(parent);
				if(clipToGrid) {
					parent.pos.x = Math.round(m.x);
					parent.pos.y = Math.round(m.y);
				} else {
					parent.pos.x = m.x;
					parent.pos.y = m.y;
				}
				parent.init();
			}
		}
		
		if(Mouse.isMouseButtonDown(1, Mouse.LOCAL) && checkCursorInside(m, parent.pos, parent.size)) {
			
			new GameobjectEditUI(parent);
			
		}
		
		lm = m;
		
	}

	@Override
	public void render() {
		
	}

	@Override
	protected void onDestroy() {
		
		selected.remove(this);
		
		if(parent.hasComponent(Renderer.class)) {
			Renderer r = ((Renderer) parent.getComponent(Renderer.class));
			r.col = new Color(r.col.r*1.25, r.col.g*1.25, r.col.b*1.25, r.col.a);
		} else if(parent.hasComponent(FinalRenderer.class)) {
			FinalRenderer r = ((FinalRenderer) parent.getComponent(FinalRenderer.class));
			r.col = new Color(r.col.r*1.25, r.col.g*1.25, r.col.b*1.25, r.col.a);
		}
		
	}
	
	private boolean checkCursorInside(Vector c, Vector pos, Vector size) {
		if(c.x < pos.x-size.x*0.5) return false;
		if(c.x > pos.x+size.x*0.5) return false;
		if(c.y < pos.y-size.y*0.5) return false;
		if(c.y > pos.y+size.y*0.5) return false;
		return true;
	}

}
