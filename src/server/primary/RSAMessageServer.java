package server.primary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingDeque;

import shared.Savable;
import shared.StateAutoSaver;
import shared.User;
import shared.Utilities;
import shared.message.ServerMessage;

/**
 * Main RSA Server implementation. Starts the server and allows a maximum number of
 * connections, each executed on different threads.
 * 
 * @author Chae Jubb
 * @version 1.1
 * 
 */
public class RSAMessageServer implements Savable {

	public final static PrintStream realStdOut = System.out;
	public final PrintWriter logOut;
	private ServerSocket serveSocket;

	private LinkedBlockingDeque<ServerMessage> messageQueue;
	private ArrayList<User> users;
	public static final File savePath = new File("./data/server/");
	public static final File dataPath = new File("./data/server/RSAmsData.dat");
	public static final File userPath = new File("./data/server/RSAmsUser.dat");
	private ThreadGroup connections;

	private long maxSubThreads = 50;
	public static final long saveStateDelayMilli = 1000 * 60 * 15; // 15 minutes

	/**
	 * Constructor
	 * 
	 * @param port
	 *          Port on which to start server
	 */
	public RSAMessageServer(int port, PrintWriter serverLog){
		this.logOut = serverLog;

		this.connections = new ThreadGroup("Connections");

		try {
			this.serveSocket = new ServerSocket(port);

			// attempts to get IP address where server is running. Defaults to
			// localhost. May or may not return loopback address.
			String ipAddress = "";
			InetAddress ip = InetAddress.getLocalHost();
			if (ip instanceof InetAddress) {
				ipAddress = ip.getHostAddress();
			} else {
				ipAddress = "localhost";
			}

			// print out IP Address and port of server
			this.logOut.println(Utilities.getTimeStamp() + "\tRSA Message Server started at " + ipAddress
					+ ":" + port);

			this.loadState();

			// set up data and users if necessary
			if (this.messageQueue == null) {
				this.messageQueue = new LinkedBlockingDeque<ServerMessage>();
			}
			if (this.users == null) {
				this.users = new ArrayList<User>();
			}

			// add interrupt catch
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run(){
					logOut.println(Utilities.getTimeStamp() + "\tEMERGENCY SAVE");
					logOut.flush();
					saveState();
					closeConnections();
				}
			});

			// schedules saving state periodically
			new Timer(true).schedule(new StateAutoSaver(this), saveStateDelayMilli, saveStateDelayMilli);

			this.waitForConnect();
		} catch (IOException e) {
			e.printStackTrace(this.logOut);
		} finally {
			this.saveState();
			this.closeConnections();
		}
	}

	/**
	 * Save message log and user database
	 */
	public void saveState(){
		try {
			if (this.messageQueue != null) {
				RSAMessageServer.savePath.mkdirs();
				FileOutputStream dataOut = new FileOutputStream(RSAMessageServer.dataPath);
				ObjectOutputStream dout = new ObjectOutputStream(dataOut);
				dout.writeObject(this.messageQueue);
				dout.close();
				dataOut.close();
			}

			if (this.users != null) {
				RSAMessageServer.savePath.mkdirs();
				FileOutputStream userOut = new FileOutputStream(RSAMessageServer.userPath);
				ObjectOutputStream uout = new ObjectOutputStream(userOut);
				uout.writeObject(this.users);
				uout.close();
				userOut.close();
			}
			this.logOut.println(Utilities.getTimeStamp() + "\tState saved");

		} catch (IOException e) {
			e.printStackTrace(this.logOut);
		}
	}

	/**
	 * Attempt to read message log and user database from file
	 */
	@SuppressWarnings("unchecked")
	private void loadState(){
		try {
			FileInputStream dataIn = new FileInputStream(RSAMessageServer.dataPath);
			FileInputStream userIn = new FileInputStream(RSAMessageServer.userPath);

			ObjectInputStream din = new ObjectInputStream(dataIn);
			ObjectInputStream uin = new ObjectInputStream(userIn);

			this.messageQueue = (LinkedBlockingDeque<ServerMessage>) din.readObject();
			this.users = (ArrayList<User>) uin.readObject();

			uin.close();
			din.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace(this.logOut);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(this.logOut);
		}
	}

	public boolean addMessage(ServerMessage in){
		return this.messageQueue.offerFirst(in);
	}

	public boolean addUser(User in){
		Iterator<User> it = this.users.iterator();
		while (it.hasNext()) {
			if (it.next().getID() == in.getID()) {
				return false;
			}
		}

		return this.users.add(in);
	}

	public boolean findUser(User in){
		Iterator<User> it = this.users.iterator();
		while (it.hasNext()) {
			if (it.next().getID() == in.getID()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check messages intended to be sent to a certain user
	 * 
	 * @param uid
	 *          ID of user
	 * @return Array of Messages addressed to certain user
	 */
	public ServerMessage[] checkForMessages(long uid){
		ArrayList<ServerMessage> tempMes = new ArrayList<ServerMessage>();

		Iterator<ServerMessage> mesIt = this.messageQueue.iterator();
		ServerMessage temp;
		while (mesIt.hasNext()) {
			if ((temp = mesIt.next()).getRecipient() == uid) {
				tempMes.add(temp);
				this.messageQueue.remove(temp);
			}
		}

		return tempMes.toArray(new ServerMessage[tempMes.size()]);
	}

	/**
	 * Method accepts connections to server. For each connection, a new thread controlling
	 * that connection is started.
	 */
	public void waitForConnect(){
		while (this.connections.activeCount() < this.maxSubThreads) {
			try {
				// start connection
				new Thread(this.connections, new RSAMessageServerWorker(this.serveSocket.accept(), this))
						.start();
			} catch (IOException e) {
				e.printStackTrace(this.logOut);
			}
		}
	}

	/**
	 * Closes socket on method call
	 */
	public void closeConnections(){
		try {
			this.serveSocket.close();
		} catch (IOException e) {
			e.printStackTrace(this.logOut);
		}
	}

}
