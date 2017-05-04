package levels;

import java.net.InetAddress;
import java.net.UnknownHostException;

import main.Game;
import main.Main;
import network.Network;
import physics.Vector;
import rendering.StringDrawer;
import ui.Button;
import ui.Text;
import ui.TextField;
import ui.Ui;

/**
 * 
 * This class is for drawing the createServer ui
 * 
 * @author jafi2
 *
 */
public class CreateServer extends Level {

	/**
	 * The ui
	 */
	private Ui ui;
	
	/**
	 * The text field for the port of the server
	 */
	private TextField tfPort = null;
	
	/**
	 * Creates a new createServer ui
	 * @throws UnknownHostException thrown if theres a problem with getting the local ip Adressa
	 */
	public CreateServer() throws UnknownHostException {
		
		ui = new Ui();
		
		Text tIp = new Text(ui);
		tIp.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-100);
		tIp.text = "Your IP-Adress: " + InetAddress.getLocalHost().getHostAddress();
		
		
		tfPort = new TextField(ui) {
			
			@Override
			protected boolean checkChar(char typed) {
				if(text.length()>=5) return false;
				if(!Character.isDigit(typed)) return false;
				if(Integer.parseInt(text.toString() + typed) > 65535) return false;
				return true;
			}
		};
		tfPort.text = new StringBuilder("" + Network.DEFAULT_PORT);
		tfPort.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-160);
		tfPort.size = new Vector(400, 40);
		
		Text tPort = new Text(ui);
		tPort.pos = new Vector(Main.windowWidth/2-400, Main.windowHeight-140-StringDrawer.letterHeight/2);
		tPort.text = "Port:";
		
		Button bCreate = new Button(ui, new Button.ClickListener() {
			
			@Override
			public void onClick() {
				Game.changeLevel(new OnlineGame(Integer.parseInt(tfPort.text.toString())));
			}
		});
		bCreate.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-220);
		bCreate.size = new Vector(400, 40);
		bCreate.text = "Create";
		
		Button bBack = new Button(ui, new Button.ClickListener() {
			
			@Override
			public void onClick() {
				Game.changeLevel(new MainMenu());
			}
		});
		bBack.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-280);
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
