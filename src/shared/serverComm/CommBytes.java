package shared.serverComm;

/**
 * Collection of bytes used by server communication.
 * 
 * {@link shared.serverComm#receiveMessage}, {@link shared.serverComm#sendMessage}, and
 * {@link shared.serverComm#addNewUser} may be all sent simultaneously by bitwise ORing
 * the desired operations.
 * 
 * Other bytes are simply sent as single bytes.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
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
