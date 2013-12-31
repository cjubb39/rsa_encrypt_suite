package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import rsaEncrypt.KeyFile;
import shared.CommBytes;
import shared.RSAMessage;
import shared.ServerMessage;
import shared.Utilities;

/**
 * Interface for connecting with RSAEncryption Server of equal version
 * 
 * @author Chae Jubb
 * @version 2.0
 *
 */
public class ServerHandler {
	
	public static boolean connectAndAct(byte action, ServerProfile server, UserProfile user, List<ServerMessage> messages, List<ServerMessage> messagesBack) throws IOException{
		byte debug = -1;
		// if no messages, we can't send anything.  Bad request
		if (messages == null && (action & CommBytes.sendMessage) != 0){
			return false;
		}
		
		// set up comm infrastructure
		Socket socket = new Socket(server.getHostname(), server.getPort());
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		
		// set up communication
		if (Utilities.receiveByte(in) != CommBytes.ready){
			closeConnection(socket);
			return false;
		}
		Utilities.sendByte(CommBytes.ready, out);
		if ((debug = Utilities.receiveByte(in)) != CommBytes.ack) {
			System.out.println("OUT: " + debug);
			closeConnection(socket);
			return false;
		}
		
		// send action
		if (Utilities.receiveByte(in) != CommBytes.ready){
			closeConnection(socket);
			return false;
		}
		Utilities.sendByte(action, out);
		if (Utilities.receiveByte(in) != CommBytes.ack) {
			closeConnection(socket);
			return false;
		}
		
		// send user file
		if (Utilities.receiveByte(in) != CommBytes.ready){
			closeConnection(socket);
			return false;
		}
		Utilities.sendData(Utilities.serializeToByteArray(user.getMe()), out);
		if (Utilities.receiveByte(in) != CommBytes.ack) {
			closeConnection(socket);
			return false;
		}
		
		
		//do authentication		
		if (Utilities.receiveByte(in) != CommBytes.ready){
			closeConnection(socket);
			return false;
		}
		Utilities.sendByte(CommBytes.ready, out);
		byte[] messageIn = Utilities.receiveData(in);
		Utilities.sendByte(CommBytes.ack, out);
		
		// construct keyfile from data in
		KeyFile kf;
		try {
			kf = (KeyFile) Utilities.deserializeFromByteArray(messageIn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			closeConnection(socket);
			return false;
		}

		// receive test sequence
		Utilities.sendByte(CommBytes.ready, out);
		byte[] testIn = Utilities.receiveData(in);
		Utilities.sendByte(CommBytes.ack, out);
		
		RSAMessage decMessage = new RSAMessage(testIn).decryptMessage(user.getKp().getPriv());
		RSAMessage sendBack = new RSAMessage(decMessage.getMessage(), true).encryptMessage(kf);
		
		//send back response
		if (Utilities.receiveByte(in) != CommBytes.ready){
			closeConnection(socket);
			return false;
		}
		Utilities.sendData(sendBack.getMessage(),out);
		if (Utilities.receiveByte(in) != CommBytes.ack) {
			closeConnection(socket);
			return false;
		}
		
		if (Utilities.receiveByte(in) != CommBytes.success){
			closeConnection(socket);
			return false;
		}
		
		/* authenticated at this point */
		
		// send messages to server if requested
		if ((action & CommBytes.sendMessage) != 0){
			for (int i = 0; i < messages.size(); i++){
				ServerMessage toSendMes = messages.get(i);
				
				RSAMessage toSend = new RSAMessage(toSendMes.getMessage(), true).
						encryptMessage(new AddressBook(user.getAddressBook()).lookupByID(toSendMes.getRecipient()).getPubKey());
				toSendMes = new ServerMessage(toSendMes.getSender(), toSendMes.getRecipient(), toSend.getMessage());
				
				messages.set(i, toSendMes);
			}
			
			if (Utilities.receiveByte(in) != CommBytes.ready){
				closeConnection(socket);
				return false;
			}
			Utilities.sendData(Utilities.serializeToByteArray(messages.toArray(new ServerMessage[messages.size()])),out);
			if (Utilities.receiveByte(in) != CommBytes.ack) {
				closeConnection(socket);
				return false;
			}
			int failCount = 0;
			for (int i = 0; i < messages.size(); i++)
				if (Utilities.receiveByte(in) == CommBytes.failure)
					failCount++;
			if (failCount > 0){
				System.err.println(failCount + "messages failed to send.  Aborting.");
				closeConnection(socket);
				return false;
			}
		}
		
		//receive from server if requested
		if ((action & CommBytes.receiveMessage) != 0){
			// set up comm for this portion
			if (Utilities.receiveByte(in) != CommBytes.ready){
				closeConnection(socket);
				return false;
			}
			Utilities.sendByte(CommBytes.ready, out);
			
			//read in messages
			ServerMessage[] messagesIn;
			try {
				messagesIn = (ServerMessage[]) Utilities.deserializeFromByteArray(Utilities.receiveData(in));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				closeConnection(socket);
				return false;
			}
			Utilities.sendByte(CommBytes.ack, out);
			
			// decrypt messages
			if (messagesIn != null && messagesIn.length > 0){
				ServerMessage[] toRet = new ServerMessage[messagesIn.length];
				for (int i = 0; i < toRet.length; i++){
					RSAMessage temp = new RSAMessage(messagesIn[i].getMessage()).decryptMessage(user.getKp().getPriv());
					messagesBack.add(new ServerMessage(messagesIn[i].getSender(), messagesIn[i].getRecipient(),
							temp.getMessage(), messagesIn[i].getDate()));
				}
			}
		}
		return true;
	}
	
	public static void closeConnection(Socket sock){
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
