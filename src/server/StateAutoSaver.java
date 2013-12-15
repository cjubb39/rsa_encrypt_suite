package server;

import java.util.TimerTask;

public class StateAutoSaver extends TimerTask{

	public static final int saveFrequencyMin = 15;
	private RSAMessageServer server;
	
	public StateAutoSaver(RSAMessageServer server){
		this.server = server;
	}
	
	// save every 15 minutes
	public void run(){
		this.server.saveState();
	}
	
}
