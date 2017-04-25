package input;

public abstract class MouseListener {
	
	public abstract void onPress(int button, int modifiers);
	public abstract void onRelease(int button, int modifiers);
	public abstract void onScroll(double delta);
	
}
