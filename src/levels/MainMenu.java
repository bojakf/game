package levels;

import java.net.UnknownHostException;

import main.Game;
import main.Main;
import mapCreator.MapCreator;
import physics.Vector;
import ui.Button;
import ui.Button.ClickListener;
import ui.Ui;

/**
 * 
 * This level is for drawing the main menu
 * 
 * @author jafi2
 *
 */
public class MainMenu extends Level {

	/**
	 * the ui of the mainMenu
	 */
	private Ui ui;
	
	/**
	 * Init a new mainMenu
	 */
	public MainMenu() {
		
		ui = new Ui();
		
		Button bCreateServer = new Button(ui, new ClickListener() {
			
			@Override
			public void onClick() {
				try {
					Game.changeLevel(new CreateServer());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		});
		bCreateServer.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-100);
		bCreateServer.size = new  Vector(400, 40);
		bCreateServer.text = "Create Server";
		
		Button bJoinServer = new Button(ui, new ClickListener() {
			
			@Override
			public void onClick() {
				Game.changeLevel(new JoinServer());
			}
		});
		bJoinServer.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-160);
		bJoinServer.size = new  Vector(400, 40);
		bJoinServer.text = "Join Server";	
		
		Button bSettings = new Button(ui, new ClickListener() {
			
			@Override
			public void onClick() {
				Game.changeLevel(new Settings());
			}
		});
		bSettings.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-220);
		bSettings.size = new Vector(400, 40);
		bSettings.text = "Settings";
		
		Button bMapCreator = new Button(ui, new ClickListener() {
			
			@Override
			public void onClick() {
				Game.changeLevel(new MapCreator());
			}
		});
		bMapCreator.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-280);
		bMapCreator.size = new Vector(400, 40);
		bMapCreator.text = "Map Creator";
		
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
