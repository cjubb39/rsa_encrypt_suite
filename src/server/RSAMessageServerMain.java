package server;

public class RSAMessageServerMain {

	/**
	 * Starts RSA Encryption Suite Server on port of first argument
	 * 
	 * @param args
	 *          args[0] should equal the integer port number on which to start the service
	 */
	public static void main(String[] args){
		new server.primary.RSAMessageServer(Integer.valueOf(args[0]));

	}

}
