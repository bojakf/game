package rendering;

import java.io.File;
import java.util.ArrayList;

import loading.TexManager;
import main.Game;

/**
 * 
 * This class is for binding and using animated textures<br>
 * The textures must have the following file structure: [location]\[number of image].png<br>
 * The first textures has to have the name 1.png
 * 
 * @author jafi2
 *
 */
public class AnimatedTexture {

	/**
	 * Value for animation timing
	 */
	private double animationTimer = 0;
	/**
	 * The current texture
	 */
	private int curTex = 0;
	/**
	 * The textures
	 */
	private String texNames[];
	/**
	 * The until the next texture is shown
	 */
	private double frameTime;
	
	/**
	 * Create a new Animated Texture
	 * @param location the location of the folder containing the images
	 * @param fps the number of frames per second
	 */
	public AnimatedTexture(String location, int fps) {
		
		frameTime = 1d/fps;
		File folder = new File(Game.gamePath + location);
		if(!folder.exists()) throw new RuntimeException("Animation does not exist: " + location);
		if(!folder.isDirectory()) throw new RuntimeException("The animation location must be a folder: " + location);
		
		String namePrefix = folder.getName();
		int c = 1;
		File f;
		ArrayList<String> texNames = new ArrayList<>();
		while((f = new File(folder.getPath() + File.separatorChar + c + ".png")).exists()) {
			TexManager.loadTex(namePrefix + "_" + c, f.getPath());
			texNames.add(namePrefix + "_" + c);
			c++;
		}
		this.texNames = new String[texNames.size()];
		for(int i = 0; i < texNames.size(); i++) {
			this.texNames[i] = texNames.get(i);
		}
		
	}
	
	/**
	 * This is used to update the animation
	 * @param deltaTime the time since the last update
	 */
	public void update(double deltaTime) {
		animationTimer += deltaTime;
		while(animationTimer > frameTime) {
			animationTimer-=frameTime;
			curTex++;
			if(curTex >= texNames.length) {
				curTex = 0;
			}
		}
	}
	
	/**
	 * This is used to bind the current texture for rendering
	 */
	public void bindCur() {
		TexManager.bindTex(texNames[curTex]);
	}
	
}
