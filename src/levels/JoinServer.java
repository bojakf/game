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
 * This class is for drawing JoinServer ui
 * 
 * @author jafi2
 *
 */
public class JoinServer extends Level {

	/**
	 * The ui
	 */
	private Ui ui;
	
	/**
	 * The text field for the serverIp
	 */
	private TextField tfServerIp = null;
	
	/**
	 * Create a new JoinServer ui
	 */
	public JoinServer() {
		
		ui = new Ui();
		
		/*
		 * TODO improve server ip formatting
		 */
		tfServerIp = new TextField(ui) {
			
			@Override
			protected boolean checkChar(char typed) {
				if(text.length() >= 15) return false;
				if(text.length()%4 == 3) {
					text.append('.');
					return false;
				}
				if(!Character.isDigit(typed)) return false;
				return true;
			}
		};
		tfServerIp.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-100);
		tfServerIp.size = new Vector(400, 40);
		
		Text tServerIP = new Text(ui);
		tServerIP.text = "Server IP:";
		tServerIP.pos = new Vector(Main.windowWidth/2-400, Main.windowHeight-80-StringDrawer.letterHeight/2);
		
		TextField tfServerPort = new TextField(ui) {
			
			@Override
			protected boolean checkChar(char typed) {
				if(text.length()>=5) return false;
				if(!Character.isDigit(typed)) return false;
				if(Integer.parseInt(text.toString() + typed) > 65535) return false;
				return true;
			}
		};
		tfServerPort.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-160);
		tfServerPort.size = new Vector(400, 40);
		tfServerPort.text = new StringBuilder("" + Network.DEFAULT_PORT);
		
		Text tServerPort = new Text(ui);
		tServerPort.text = "Server Port:";
		tServerPort.pos = new Vector(Main.windowWidth/2-400, Main.windowHeight-140-StringDrawer.letterHeight/2);
		
		Button bJoin = new Button(ui, new Button.ClickListener() {
			
			@Override
			public void onClick() {
				try {
					Game.changeLevel(new OnlineGame(InetAddress.getByName(tfServerIp.text.toString()), Integer.parseInt(tfServerPort.text.toString())));
				} catch (NumberFormatException | UnknownHostException e) {
					e.printStackTrace();
				}
			}
		});
		bJoin.pos = new Vector(Main.windowWidth/2-200, Main.windowHeight-220);
		bJoin.size = new Vector(400, 40);
		bJoin.text = "Join";
		
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
	}

}
