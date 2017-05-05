package levels;

import org.lwjgl.glfw.GLFW;

import main.Game;
import main.Main;
import physics.Vector;
import ui.Button;
import ui.Ui;

/**
 * 
 * This level is for drawing the Settings menu
 * 
 * @author jafi2
 *
 */
public class Settings extends Level {
	
	/**
	 * The ui to draw in
	 */
	private Ui ui;
	
	/**
	 * The button for turning vSync on and off
	 */
	private Button bVSync;
	
	/**
	 * Create a new Settings menu
	 */
	public Settings() {
		ui = new Ui();
		
		bVSync = new Button(ui, new Button.ClickListener() {
			
			@Override
			public void onClick() {
				if(Main.isVSync) {
					GLFW.glfwSwapInterval(0);
					Main.isVSync = false;
					bVSync.text = "Enable vSync";
				} else {
					GLFW.glfwSwapInterval(1);
					Main.isVSync = true;
					bVSync.text = "Disable vSync";
				}
			}
		});
		bVSync.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-100);
		bVSync.size = new Vector(400, 40);
		bVSync.text = "Disable vSync";
		
		Button bBack = new Button(ui, new Button.ClickListener() {
			
			@Override
			public void onClick() {
				Game.changeLevel(new MainMenu());
			}
		});
		bBack.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-160);
		bBack.size = new Vector(400, 40);
		bBack.text = "Back";
		
	}
	
	@Override
	public void update(double deltaTime) {
		ui.update(deltaTime);		
	}

	@Override
	public void render() {
		ui.render();
	}

	@Override
	public void onClose() {
		ui.destroy();
	}

}
