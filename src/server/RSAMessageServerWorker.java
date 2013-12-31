package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

import rsaEncrypt.KeyPair;
import rsaEncrypt.MakeKeys;
import shared.CommBytes;
import shared.RSAMessage;
import shared.ServerMessage;
import shared.User;
import shared.Utilities;

/**
 * Controller of connections with RSA server
 * 
 * @author Chae Jubb
 * @version 2.0
 * 
 */
public class RSAMessageServerWorker implements Runnable {

	private Socket client;
	private OutputStream out;
	private InputStream in;
	private RSAMessageServer mainServer;
	private Random rng;

	/**
	 * Constructor.  Creates necessary SOcket, PrintWriter, Scanner.
	 * @param client Client of connection
	 * @param mainServer Server attached to.
	 */
	public RSAMessageServerWorker(Socket client, RSAMessageServer mainServer) {
		this.mainServer = mainServer;
		this.client = client;
		System.out.println("Client " + this.client.getInetAddress().getHostAddress() + " Connected");

		try {
			this.in = this.client.getInputStream();
			this.out = this.client.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.rng = new Random(System.nanoTime());
	}

	public void run() {
		byte action = 0;
		User user = null;
	
		byte debug = -1;
		
		try{
			// start comm with ready byte and send ack byte
			Utilities.sendByte(CommBytes.ready, this.out);
			if ((debug = Utilities.receiveByte(this.in)) != CommBytes.ready){
				System.out.println("OUT: " + debug);
				return;
			}
			Utilities.sendByte(CommBytes.ack, this.out);
			
			// get intentions
			Utilities.sendByte(CommBytes.ready, this.out);
			action = Utilities.receiveByte(this.in);
			Utilities.sendByte(CommBytes.ack, this.out);
			
			//get user; if bad, end connection
			Utilities.sendByte(CommBytes.ready, this.out);
			if ((user = this.retrieveUser()) == null){
				this.closeConnections();
				return;
			}
			Utilities.sendByte(CommBytes.ack, this.out);
			
			//authenticate
			this.authenticate(user);
			
			// wants to add user
			if ((action & CommBytes.addNewUser) != 0){
				this.addUser(user);
			}
			
			// wants to send a message, so we receive it.
			if ((action & CommBytes.sendMessage) != 0){
				this.receiveMessage(user);
			}
			
			// wants to receive his/her messages, so we send them
			if ((action & CommBytes.receiveMessage) != 0){
				this.sendMessage(user);
			}
			
			Utilities.sendByte(CommBytes.success, this.out);
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	private void addUser(User user){
		if (!this.mainServer.findUser(user))
			this.mainServer.addUser(user);
	}
	
	private void receiveMessage(User user) throws IOException{
		ServerMessage[] messagesIn;
		
		Utilities.sendByte(CommBytes.ready, this.out);
		try {
			messagesIn = (ServerMessage[]) Utilities.deserializeFromByteArray(Utilities.receiveData(this.in));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Utilities.sendByte(CommBytes.ack, this.out);
		
		for (ServerMessage sm : messagesIn){
			System.out.print("[INCOMING] F: " + sm.getSender() + "; T: " + sm.getRecipient() + "; D: " + sm.getDate());
			if (sm.getSender() == user.getID()){
				this.mainServer.addMessage(sm);
				Utilities.sendByte(CommBytes.success, this.out);
				System.out.println(" [PASS]");
			} else {
				Utilities.sendByte(CommBytes.failure, this.out);
				System.out.println(" [FAIL]");
			}
		}
	}
	
	private void sendMessage(User user) throws IOException {
		// send ready byte
		Utilities.sendByte(CommBytes.ready, this.out);
		
		// wait for ready byte in return
		if (Utilities.receiveByte(this.in) != CommBytes.ready){
			return;
		}
		
		//look for messages and send them;
		ServerMessage[] outgoing = this.mainServer.checkForMessages(user.getID());
		
		for (ServerMessage sm : outgoing){
			System.out.println("[OUTGOING] F: " + sm.getSender() + "; T: " + sm.getRecipient() + "; D: " + sm.getDate());
		}
		
		Utilities.sendData(Utilities.serializeToByteArray(outgoing), this.out);
		
		//read ack byte;
		if (Utilities.receiveByte(this.in) != CommBytes.ack){
			return;
		}
	}

	/**
	 * Get user from client
	 * @return User session initiated with
	 * @throws IOException
	 */
	public User retrieveUser() throws IOException{
		User curUser = null;
		
		//get data from stream
		byte[] messageIn = shared.Utilities.receiveData(this.client.getInputStream());
		
		try {
			curUser = (User) Utilities.deserializeFromByteArray(messageIn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}	
		return curUser;
	}	
	
	private User authenticate(User readIn) throws IOException {
		// let client know we're starting
		Utilities.sendByte(CommBytes.ready, this.out);
		
		// send public key for test
		while(Utilities.receiveByte(this.in) != CommBytes.ready);

		//verifying identity with test
		KeyPair kp = MakeKeys.generateKeys();	
		RSAMessage test = new RSAMessage(new BigInteger(kp.getPub().getGroupSize().bitLength()/8, this.rng).toByteArray(), true);
		RSAMessage testMes = test.encryptMessage(readIn.getPubKey());
		Utilities.sendData(shared.Utilities.serializeToByteArray(kp.getPub()), this.out);
	

		while(Utilities.receiveByte(this.in) != CommBytes.ack);
		
		
		// send test sequence
		while(Utilities.receiveByte(this.in) != CommBytes.ready);
		shared.Utilities.sendData(testMes.getMessage(), this.out);
		while(Utilities.receiveByte(this.in) != CommBytes.ack);
		
		// read response
		Utilities.sendByte(CommBytes.ready, this.out);
		byte[] messageIn = Utilities.receiveData(this.in);
		Utilities.sendByte(CommBytes.ack, this.out);
		
		// decrypt and check validity
		RSAMessage retMessage = new RSAMessage(messageIn).decryptMessage(kp.getPriv());
		BigInteger returned = new BigInteger(retMessage.getMessage());

		if(returned.xor(new BigInteger(test.getMessage())).equals(BigInteger.ZERO)){
			Utilities.sendByte(CommBytes.success, this.out);
			System.out.println("Authenticated: " + readIn.getID());
			return readIn;
		} else {
			Utilities.sendByte(CommBytes.failure, this.out);
			return null;
		}
	}
	
	

	/**
	 * Close PrintWriter, Scanner, and Socket
	 */
	private void closeConnections() {
		try {
			Utilities.sendByte(CommBytes.hangup, this.out);
			this.out.close();
			this.in.close();
			System.out.println("Client " +this.client.getInetAddress().getHostAddress() + " Disconnected");
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
