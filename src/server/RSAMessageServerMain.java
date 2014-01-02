package server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class RSAMessageServerMain {

	/**
	 * Starts RSA Encryption Suite Server on port of first argument
	 * 
	 * @param args
	 *          args[0] should equal the integer port number on which to start the service
	 *          args[1] should have the server logfile name to write to
	 * @throws NumberFormatException
	 *           Port specified is not a number
	 * @throws FileNotFoundException
	 *           Error opening specified logfile
	 */
	public static void main(String[] args) throws NumberFormatException, FileNotFoundException{
		FileOutputStream fileOS = new FileOutputStream(args[1], true);
		new server.primary.RSAMessageServer(Integer.valueOf(args[0]), new PrintWriter(fileOS, true));

	}

}
