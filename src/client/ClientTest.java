package client;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.math.*;

import shared.*;
import rsaEncrypt.*;

public class ClientTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws UnknownHostException,
			IOException, ClassNotFoundException {
		String hostname = "localhost";
		int port = 4444;

		String message = "Scott Disick";
		rsaEncrypt.KeyPair kp = setKP();
		rsaEncrypt.KeyPair kp2 = MakeKeys.generateKeys();
//System.out.println("N: " + kp.getPub().getGroupSize() + "; E: " + kp.getPub().getKey());	
		User me = new User("Chae Jubb", kp.getPub());
		User otherUser = new User("Test User", kp2.getPub());
		ArrayList<User> userArray = new ArrayList<User>();
		userArray.add(me); userArray.add(otherUser);
		
		ArrayList<ServerMessage> mesArray = new ArrayList<ServerMessage>();
		mesArray.add(new ServerMessage(me, me, message.getBytes()));
		mesArray.add(new ServerMessage(me, otherUser, "Lord Disick".getBytes()));
		mesArray.add(new ServerMessage(otherUser, me, "Check check check".getBytes()));

		
		for (User user : userArray) {
			Socket sock = new Socket(hostname, port);
			PrintWriter output = new PrintWriter(sock.getOutputStream(), true);
			Scanner input = new Scanner(sock.getInputStream());

			// add user
			input.next(); // newusercheck
			output.println("YES"); // we are new user
			input.next(); // ready get get new user
			shared.Utilities.sendData(shared.Utilities.serializeToByteArray(user), sock.getOutputStream());
			input.next(); // connection end marker
			System.out.println("Add User successful");
		}

/*************************************************************/
		
		// send messages
		for (ServerMessage toSendMes : mesArray) {

			KeyPair k = null; User user = null;
			if(toSendMes.getSender() == me.ID){
				k = kp;
				user = me;
			} else if (toSendMes.getSender() == otherUser.ID){
				k = kp2;
				user = otherUser;
			}
			
			Socket socket = new Socket(hostname, port);
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
			shared.Utilities.sendData(shared.Utilities.serializeToByteArray(user),
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
				continue;
			} else {
				// System.out.println("USER FOUND BY SERVER");
				out.println("ACK");
				// user found
				// get server public key
				 
				byte[] messageIn = shared.Utilities.receiveData(socket
						.getInputStream());
				KeyFile kf = (KeyFile) shared.Utilities.deserializeFromByteArray(messageIn);
				// System.out.println("N: " + kf.getGroupSize() + "; E: " +
				// kf.getKey());
				out.println("ACK");
				// System.out.println("ACK-SENT");

				// get test message
				 
				byte[] testIn = shared.Utilities.receiveData(socket
						.getInputStream());
				// testIn = shared.Utilities.trimByteArray(testIn);
				// System.out.println("READ IN");
				// kf = (KeyFile) shared.Utilities.deserialize(messageIn);

				RSAMessage decMessage = new RSAMessage(testIn).decryptMessage(k.getPriv());
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
				continue;
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
		}
		/**************************************************************/

		/**************************************************************/
		
		for (User user : userArray) {
			KeyPair k = null;
			if (user.equals(me)) {
				k = kp;
			} else if (user.equals(otherUser)) {
				k = kp2;
			}

			Socket socket = new Socket(hostname, port);
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
			shared.Utilities.sendData(shared.Utilities.serializeToByteArray(user),
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
				return;
			} else {
				// System.out.println("USER FOUND BY SERVER");
				out.println("ACK");
				// user found
				// get server public key
				
				 // KeyFile kf; byte[] messageIn = new byte[1024*1024]; //1MB max
				 //keysize socket.getInputStream().read(messageIn);
				 // //System.out.println("IS READ"); messageIn =
				 // shared.Utilities.trimByteArray(messageIn);
				 
				byte[] messageIn = shared.Utilities.receiveData(socket
						.getInputStream());
				KeyFile kf = (KeyFile) shared.Utilities.deserializeFromByteArray(messageIn);
				// System.out.println("N: " + kf.getGroupSize() + "; E: " +
				// kf.getKey());
				out.println("ACK");
				// System.out.println("ACK-SENT");

				// get test message
				
				 //byte[] testIn = new byte[1024*1024]; //1MB max testMessage
				 //socket.getInputStream().read(testIn);
				 
				byte[] testIn = shared.Utilities.receiveData(socket
						.getInputStream());
				// testIn = shared.Utilities.trimByteArray(testIn);
				// System.out.println("READ IN");
				// kf = (KeyFile) shared.Utilities.deserialize(messageIn);
				RSAMessage decMessage = new RSAMessage(testIn).decryptMessage(k
						.getPriv());
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
				return;
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
			 
			ServerMessage[] messagesIn;// = (ServerMessage[])
										// shared.Utilities.deserialize(socket.getInputStream());
			messagesIn = (ServerMessage[]) shared.Utilities.deserializeFromByteArray(shared.Utilities.receiveData(socket
							.getInputStream()));
			out.println("READY TO CLOSE");

			in.next(); // acknowledge close
			
			// close out connection
			out.close();
			in.close();
			socket.close();
			
			System.out.println("Messages for " + user.getFirstName() + ": ");
			int i = 0;
			for (ServerMessage sm : messagesIn) {
				System.out.println("MES " + i++ + ": "
						+ new String(sm.getMessage()));
			}
		}
	}
	
	/*public static void printByteArray(byte[] in){
		for (byte b: in){
			System.out.println("BYTE: " + b);
		}
	}*/
	
	public static KeyPair setKP(){
		BigInteger N = new BigInteger("566015045949848586808153526116220596154504517416986834031708602412372022090973364627303179774654843562132689692481015099486328912362830558555917506007626716127926641890307488752493591022225295020070358901257175428223025252181182703582791356267866064884721222119928799790392379687590846166249577266355109423355970285170442348795957291169854604067679200001046697865110294693890015958568466270659799020478775009189226467268605289613481772922653825002708917373103992847737806960788527330845117861881091197943179290736994176066450059069909836436616128190510628411805205307646312417893762555604798410091166730501037360508094349972947070411921929916975720756499875652472192424564019747440062066735898549801790402306724915959190433580944833332384299790375259292815430416895041359928114620991343844112541897073271933018599522380933602516059476597623336389820730680345907890243151085139630355863384945583221434951");
		BigInteger E = new BigInteger("5");
		BigInteger D = new BigInteger("113203009189969717361630705223244119230900903483397366806341720482474404418194672925460635954930968712426537938496203019897265782472566111711183501201525343225585328378061497750498718204445059004014071780251435085644605050436236540716558271253573212976944244423985759958078475937518169233249915453271021884671194057034088469759191458233970920813535840000209339573022058938778003191713693254131959804095755001837845293453721057922696354584530765000541773801461146568239462648371277302520314773110456713823933520440696454631878097948571736586893416109660285811687360794971239571487048651489766761173647910920232665724643583979980659393373953040159914012393637249026129066374297855392944539745912447767463089653483430318743042579848133095267067127983324206408581614213280962494581889317712552844348630413076897236864944309984877505719000689583811411741522423651915566476394319159646311568379689250121969061");
		
		return new KeyPair(new KeyFile(N, E), new KeyFile(N,D));
	}
}
