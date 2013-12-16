package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import rsaEncrypt.KeyFile;
import shared.RSAMessage;
import shared.ServerAckMessage;
import shared.ServerMessage;

public class ServerHandler {

	public static boolean sendMessage(UserProfile user, ServerMessage toSendMes, ServerProfile server) throws IOException{
		Socket socket = new Socket(server.getHostname(), server.getPort());
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		Scanner in = new Scanner(socket.getInputStream());

		while (!in.hasNext(ServerAckMessage.newUserCheck)) {
			if (in.hasNext()) {
				in.next();
			}
		}
		in.next(); // eat check
		out.println("NO");

		while (!in.hasNext(ServerAckMessage.readyForAuth)) {
			if (in.hasNext()) {
				in.next();
			}
		}

		in.next(); // eat GOAUTH

		// send user file
		// socket.getOutputStream().write(shared.Utilities.serialize(user));
		shared.Utilities.sendData(shared.Utilities.serializeToByteArray(user.getMe()),
				socket.getOutputStream());
		
		while (!in.hasNext(ServerAckMessage.userNotFound)
				&& !in.hasNext(ServerAckMessage.userFound)) {
			if (in.hasNext()) {
				in.next();
			}
		}
		 

		if (in.next().equals(ServerAckMessage.userNotFound)) {
			System.err.println("USER NOT FOUND BY SERVER");
			out.println("ACK");
			return false;
		} else {
			// System.out.println("USER FOUND BY SERVER");
			out.println("ACK");
			// user found
			// get server public key
			 
			byte[] messageIn = shared.Utilities.receieveData(socket
					.getInputStream());
			KeyFile kf;
			try {
				kf = (KeyFile) shared.Utilities.deserializeFromByteArray(messageIn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			// System.out.println("N: " + kf.getGroupSize() + "; E: " +
			// kf.getKey());
			out.println("ACK");
			// System.out.println("ACK-SENT");

			// get test message
			 
			byte[] testIn = shared.Utilities.receieveData(socket
					.getInputStream());
			// testIn = shared.Utilities.trimByteArray(testIn);
			// System.out.println("READ IN");
			// kf = (KeyFile) shared.Utilities.deserialize(messageIn);

			RSAMessage decMessage = new RSAMessage(testIn).decryptMessage(user.getKp().getPriv());
			// printByteArray(decMessage.getMessage());
			// System.out.println("DECRYPTED: " + new
			// BigInteger(decMessage.getMessage()).toString());
			RSAMessage sendBack = new RSAMessage(decMessage.getMessage(), true)
					.encryptMessage(kf);
			shared.Utilities.sendData(sendBack.getMessage(),
					socket.getOutputStream());
		}

		while (!in.hasNext(ServerAckMessage.failure)
				&& !in.hasNext(ServerAckMessage.success)) {
			if (in.hasNext()) {
				in.next();
			}
		}

		if (in.next().equals(ServerAckMessage.failure)) {
System.out.println("Authentication Failure");
			out.println("ACK");
			return false;
		}
System.out.println("Successful Authentication");
		while (!in.hasNext("GOACTION")) {
			if (in.hasNext()) {
				in.next();
			}
		}

		in.next(); // eat GOACTION
		

		// send message
		out.println("SEND");

		while (!in.hasNext("READY")) {
			if (in.hasNext()) {
				in.next();
			}
		}

		in.next(); // eat READY

		shared.Utilities.sendData(shared.Utilities.serializeToByteArray(toSendMes), socket.getOutputStream());
		socket.getOutputStream().write(
				shared.Utilities.serializeToByteArray(toSendMes));

		String sendResponse = in.next();
		System.out.println("sendResponse: " + sendResponse);
		
		return sendResponse.equals(ServerAckMessage.success);
	}
	
	public static void addUser(UserProfile user, ServerProfile server) throws IOException {
		Socket sock = new Socket(server.getHostname(), server.getPort());
		PrintWriter output = new PrintWriter(sock.getOutputStream(), true);
		Scanner input = new Scanner(sock.getInputStream());

		// add user
		input.next(); // newusercheck
		output.println("YES"); // we are new user
		input.next(); // ready get get new user
		shared.Utilities.sendData(shared.Utilities.serializeToByteArray(user.getMe()), sock.getOutputStream());
		input.next(); // connection end marker
		System.out.println("Add User successful");
	}
	
	public static ServerMessage[] receiveMessages(UserProfile user, ServerProfile server) throws IOException {
		Socket socket = new Socket(server.getHostname(), server.getPort());
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		Scanner in = new Scanner(socket.getInputStream());

		while (!in.hasNext(ServerAckMessage.newUserCheck)) {
			if (in.hasNext()) {
				in.next();
			}
		}
		in.next(); // eat check
		out.println("NO");

		while (!in.hasNext(ServerAckMessage.readyForAuth)) {
			if (in.hasNext()) {
				in.next();
			}
		}

		in.next(); // eat GOAUTH

		// send user file
		// socket.getOutputStream().write(shared.Utilities.serialize(user));
		shared.Utilities.sendData(shared.Utilities.serializeToByteArray(user.getMe()),
				socket.getOutputStream());
		
		while (!in.hasNext(ServerAckMessage.userNotFound)
				&& !in.hasNext(ServerAckMessage.userFound)) {
			if (in.hasNext()) {
				in.next();
			}
		}
		 

		if (in.next().equals(ServerAckMessage.userNotFound)) {
			System.err.println("USER NOT FOUND BY SERVER");
			out.println("ACK");
			return null;
		} else {
			// System.out.println("USER FOUND BY SERVER");
			out.println("ACK");
			// user found
			// get server public key
			
			 // KeyFile kf; byte[] messageIn = new byte[1024*1024]; //1MB max
			 //keysize socket.getInputStream().read(messageIn);
			 // //System.out.println("IS READ"); messageIn =
			 // shared.Utilities.trimByteArray(messageIn);
			 
			byte[] messageIn = shared.Utilities.receieveData(socket
					.getInputStream());
			KeyFile kf;
			try {
				kf = (KeyFile) shared.Utilities.deserializeFromByteArray(messageIn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			// System.out.println("N: " + kf.getGroupSize() + "; E: " +
			// kf.getKey());
			out.println("ACK");
			// System.out.println("ACK-SENT");

			// get test message
			
			 //byte[] testIn = new byte[1024*1024]; //1MB max testMessage
			 //socket.getInputStream().read(testIn);
			 
			byte[] testIn = shared.Utilities.receieveData(socket
					.getInputStream());
			// testIn = shared.Utilities.trimByteArray(testIn);
			// System.out.println("READ IN");
			// kf = (KeyFile) shared.Utilities.deserialize(messageIn);
			RSAMessage decMessage = new RSAMessage(testIn).decryptMessage(user.getKp().getPriv());
			// printByteArray(decMessage.getMessage());
			// System.out.println("DECRYPTED: " + new
			// BigInteger(decMessage.getMessage()).toString());
			RSAMessage sendBack = new RSAMessage(decMessage.getMessage(), true)
					.encryptMessage(kf);
			shared.Utilities.sendData(sendBack.getMessage(),
					socket.getOutputStream());
		}

		while (!in.hasNext(ServerAckMessage.failure)
				&& !in.hasNext(ServerAckMessage.success)) {
			if (in.hasNext()) {
				in.next();
			}
		}

		if (in.next().equals(ServerAckMessage.failure)) {
System.out.println("Authentication Failure");
			out.println("ACK");
			return null;
		}
System.out.println("Successfully authenticated");
		while (!in.hasNext("GOACTION")) {
			if (in.hasNext()) {
				in.next();
			}
		}

		in.next(); // eat GOACTION

		// send message
		out.println("RECEIVE");

		
		 //while (!in.next().equals(ServerAckMessage.readyToRecieve)) { if
		 //(in.hasNext()) { System.out.println("IN LOOP: " + in.next()); } }
		 

		in.next(); // eat BEREADYTORECEIVE
		out.println("ACK");
		// read in
		
		 //byte[] messagesInBytes = new byte[ServerMessage.MAX_SIZE];
		 //socket.getInputStream().read(messagesInBytes);
		 
		ServerMessage[] messagesIn = null;// = (ServerMessage[])
									// shared.Utilities.deserialize(socket.getInputStream());
		try {
			messagesIn = (ServerMessage[]) shared.Utilities.deserializeFromByteArray(shared.Utilities.receieveData(socket
							.getInputStream()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		out.println("READY TO CLOSE");

		in.next(); // acknowledge close
		
		// close out connection
		out.close();
		in.close();
		socket.close();
		
		/*System.out.println("Messages for " + user.getMe().getFirstName() + ": ");
		int i = 0;
		for (ServerMessage sm : messagesIn) {
			System.out.println("MES " + i++ + ": "
					+ new String(sm.getMessage()));
		}*/
		
		return messagesIn;
	}
}
