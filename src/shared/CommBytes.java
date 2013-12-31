package shared;

public final class CommBytes {

	public static final byte receiveMessage = 0x1;
	public static final byte sendMessage = 0x2;
	public static final byte addNewUser = 0x4;
	
	public static final byte success = 0x7f;
	public static final byte failure = ~success;
	
	public static final byte ack = 0x55;
	public static final byte ready = ~ack;
	
	public static final byte hangup = 0x3c;
}
