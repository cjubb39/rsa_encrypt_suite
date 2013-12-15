package shared;

public class ServerAckMessage {

	public static final String readyForAction = "GOACTION";
	public static final String readyForAuth = "GOAUTH";
	public static final String endingConnection = "ENDINGCONNECTION";
	public static final String newUserCheck = "NEW";
	public static final String readyForNewUser = "SENDNEWUSERFILE";
	public static final String readyToRecieve = "BEREADYTORECIEVE";
	public static final String affirmative = "YES";
	public static final String negative = "NO";
	
	public static final String success = "SUCCESS";
	public static final String failure = "FAILURE";
	
	public static final String generalReady = "READY";
	
	public static final String userNotFound = "USERNOTFOUND";
	public static final String userFound = "USERFOUND";
	
	public static final String ack = "ACK";
}
