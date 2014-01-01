package shared;

import java.util.TimerTask;

/**
 * An TimerTask used to autosave a Savable object
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class StateAutoSaver extends TimerTask {

	private Savable object;

	/**
	 * Constructor
	 * 
	 * @param object
	 *          Object to save
	 */
	public StateAutoSaver(Savable object){
		this.object = object;
	}

	/**
	 * When timer is run, save the state of the Savable object
	 * 
	 * @see java.util.TimerTask#run()
	 */
	public void run(){
		this.object.saveState();
	}

}
