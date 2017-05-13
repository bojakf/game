package components;

import gameobject.Component;
import main.Game;

public abstract class FinalNetComponent extends Component {

	@Override
	public void start() {
		if(Game.net == null) {
			try {
				throw new RuntimeException("No network found");
			} catch(RuntimeException e) {
				e.printStackTrace();
				return;
			}
		}
		
		Game.net.add(this);
	}

}
