package shared;

import java.util.TimerTask;

public class StateAutoSaver extends TimerTask{

	private Savable object;
	
	public StateAutoSaver(Savable object){
		this.object = object;
	}
	
	// save every 15 minutes
	public void run(){
		this.object.saveState();
	}
	
}
