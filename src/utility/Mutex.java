package utility;

/**
 * 
 * Class for handling mutal exclusions
 * 
 * @author jafi2
 *
 */
public class Mutex {
	
	/**
	 * Value indicating if a thread is in the critical part of the program
	 */
	private volatile boolean b = false;
	
	/**
	 * Must be called before the thread enters the critical part
	 */
	public void P() {
		
		if(b) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		b = true;
		
	}
	
	/**
	 * Method for accessing critical program parts only if they are accessible.<br>
	 * <b>Must only be used like this: </b><br>
	 * if(mutex.PSkip()) {<br>
	 * 	  //Critical part here<br>
	 * 	  mutex.V();<br>
	 * }
	 * @return can the critical part be entered
	 */
	public boolean PSkip() {
		if(b) {
			return false;
		} else {
			b = true;
			return true;
		}
	}
	
	/**
	 * Must be called after the thread left the critical part
	 */
	public void V() {
		
		b = false;
		synchronized (this) {
			notify();
		}
		
	}
	
}