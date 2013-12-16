package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import rsaEncrypt.KeyPair;
import rsaEncrypt.MakeKeys;
import shared.RSAMessage;
import shared.ServerAckMessage;
import shared.ServerMessage;
import shared.User;
import shared.Utilities;

/**
 * Controller of connections with RSA server
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class RSAMessageServerWorker implements Runnable {

	private Socket client;
	private PrintWriter out;
	private Scanner in;
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
			this.out = new PrintWriter(this.client.getOutputStream(), true);
			this.in = new Scanner(this.client.getInputStream());
			this.in.useDelimiter(System.getProperty("line.separator"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.rng = new Random(System.nanoTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (this.newUser()) {
			this.addNewUser();
		} else {
			// get user
			User curUser = null;
			try {
				curUser = this.authenticate();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// if error authenticating, quit
			if (curUser == null) {
				System.out.println("Failure authentication");
				this.out.println(ServerAckMessage.failure);
				this.in.next(); // get confirmation
				this.closeConnections();
				return;
			}
			System.out.println("Successful authentication from " + curUser.getFirstName());
			this.out.println(ServerAckMessage.success);
			
			
			// get action wanted
			this.out.println(ServerAckMessage.readyForAction);
			String request = null;
			if ((request = this.in.next()).equals("SEND")) {
				// attempt to get message
				try {
					this.receiveMessage(curUser.getID());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (request.equals("RECEIVE")) {
				try {
					this.checkMessages(curUser.getID());
					this.in.next(); // get ready to close signal
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.out.println(ServerAckMessage.endingConnection);
		this.closeConnections();
	}
	
	private User authenticate() throws IOException {
		// let client know we're starting
		this.out.println(ServerAckMessage.readyForAuth);

		// look for user
		User readIn = this.retrieveUser();
		if (!this.mainServer.findUser(readIn)){
			//user not found
			System.out.println("Did not find user: " + readIn.getFirstName());
			this.out.println(ServerAckMessage.userNotFound);
			return null;
		} else {
			System.out.println("Found user: " + readIn.getFirstName());
			this.out.println(ServerAckMessage.userFound);
			
			while(!this.in.hasNext(ServerAckMessage.ack)){
				if (this.in.hasNext())
					this.in.next(); 
			}
			this.in.next(); //get ready signal

			//verifying identity with test
			KeyPair kp = MakeKeys.generateKeys();	
			RSAMessage test = new RSAMessage(new BigInteger(kp.getPub().getGroupSize().bitLength()/8, this.rng).toByteArray(), true);
			RSAMessage testMes = test.encryptMessage(readIn.getPubKey());
		
			// send public key
			shared.Utilities.sendData(shared.Utilities.serializeToByteArray(kp.getPub()), this.client.getOutputStream());
		
			//recieve ack that first part received
			this.in.next();
			
			// send test sequence
			shared.Utilities.sendData(testMes.getMessage(), this.client.getOutputStream());
	
			// read response
			byte[] messageIn = shared.Utilities.receieveData(this.client.getInputStream());
			
			RSAMessage retMessage = new RSAMessage(messageIn).decryptMessage(kp.getPriv());
			BigInteger returned = new BigInteger(retMessage.getMessage());

			if(returned.xor(new BigInteger(test.getMessage())).equals(BigInteger.ZERO)){
				return readIn;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Asks client if trying to add new user
	 * @return True if trying to add new user.  False otherwise
	 */
	public boolean newUser(){
		this.out.println(ServerAckMessage.newUserCheck);
		return this.in.next().equals(ServerAckMessage.affirmative);
	}
	
	/**
	 * Actually adds new user to database
	 */
	public void addNewUser(){
		this.out.println(ServerAckMessage.readyForNewUser);
		User toAdd;
		try {
			toAdd = this.retrieveUser();
		} catch (IOException e) {
			e.printStackTrace();
			this.out.println(ServerAckMessage.failure);
			return;
		}
		
		if (this.mainServer.addUser(toAdd)){
			this.out.println(ServerAckMessage.success);
		} else {
			this.out.println(ServerAckMessage.failure);
		}
	}

	/**
	 * Receives a message from client
	 * @throws IOException
	 */
	public void receiveMessage(long uid) throws IOException {
		this.out.println(ServerAckMessage.generalReady);

		// gets and deserializes message
		byte[] messageIn = shared.Utilities.receieveData(this.client.getInputStream());
		ServerMessage newMessage = null;
		try {
			newMessage = (ServerMessage) Utilities.deserializeFromByteArray(messageIn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// make sure only sender is actually sending
		if (newMessage.getSender() != uid){
			newMessage = null;
		}
		
		// adds message to server if necessary
		String response = null;
		if (newMessage != null){
			if(this.mainServer.addMessage(newMessage)){
				response = ServerAckMessage.success;
			} else {
				response = ServerAckMessage.failure;
			}
		} else {
			response = ServerAckMessage.failure;
		}
		
		// sends client success or failure
		this.out.println(response);
	}
	
	public void checkMessages(long uid) throws IOException{
		// get messages to send
		ServerMessage[] toRet = this.mainServer.checkForMessages(uid);
		
		//make sure client is ready
		this.out.println(ServerAckMessage.readyToRecieve);
		this.in.next(); // get acknowledgment
		
		//send messages
		shared.Utilities.sendData(shared.Utilities.serializeToByteArray(toRet), this.client.getOutputStream());
	}
	
	/**
	 * Get user from client
	 * @return User session initiated with
	 * @throws IOException
	 */
	public User retrieveUser() throws IOException{
		User curUser = null;
		
		//get data from stream
		byte[] messageIn = shared.Utilities.receieveData(this.client.getInputStream());
		
		try {
			curUser = (User) Utilities.deserializeFromByteArray(messageIn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}	
		return curUser;
	}

	/**
	 * Close PrintWriter, Scanner, and Socket
	 */
	private void closeConnections() {
		try {
			this.out.close();
			this.in.close();
			System.out.println("Client " +this.client.getInetAddress().getHostAddress() + " Disconnected");
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
