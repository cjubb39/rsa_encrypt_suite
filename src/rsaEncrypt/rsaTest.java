package rsaEncrypt;

import shared.*;

public class rsaTest {

	public static void main(String[] args){
		KeyPair kp = MakeKeys.generateKeys();
		//KeyPair kp = new KeyPair(new KeyFile(7135981, 3), new KeyFile(7135981, 4753643)); 
		//KeyPair kp = new KeyPair(new KeyFile(7093843, 3), new KeyFile(7093843, 4725659)); 
		
		System.out.printf("N: %d E: %d D: %d\n", 
				kp.getPub().getGroupSize(), 
				kp.getPub().getKey(), 
				kp.getPriv().getKey());
		
		Message m = new Message("{}[]Hello world.  Scott Disick.  Testing, testing, testing. ()-=", true), enc, dec;
		//Message m = new Message("Hello world. This is a sentence.  The quick brown fox jumps over the lazy dog."), enc, dec;
		enc = m.encryptMessage(kp.getPub());
		//System.out.println("INT enc: " + enc.toString());
		dec = enc.decryptMessage(kp.getPriv());
		//dec = null;
		
		System.out.printf("Message: %s\n" +
				"Enc: %s\n" +
				"Dec: %s\n", m.toString(), new String(enc.getMessage()), dec);
		
		
		//byte[] temp = Arrays.copyOfRange(m, count, count + chunkSize);
		//BigInteger data = BigInteger.valueOf(ByteBuffer.wrap(temp).getLong());
	/*	int chunkSize = 10;
		byte[] temp = new byte[chunkSize];
		BigInteger data = new BigInteger(temp);
		byte[] temp2 = data.toByteArray();
		byte[] toWrite = ByteBuffer.allocate(chunkSize).put(data.toByteArray()).array();
		
		Message.printByteArray(temp);
		System.out.println();
		Message.printByteArray(temp2);
		System.out.println();
		Message.printByteArray(toWrite);*/
	}
	
	public static String printByteArray(byte[] in){
		String toRet = "";
		for (byte b : in){
			toRet += b;
		}
		
		return toRet;
	}
}
