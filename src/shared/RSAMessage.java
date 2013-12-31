package shared;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import rsaEncrypt.KeyFile;

/**
 * Wrapper for byte array.  To be used to send byte array messages
 * @author Chae Jubb
 * @version 1.0
 *
 */
public class RSAMessage implements Serializable {

	private static final long serialVersionUID = 8967541811335342735L;
	public static final int MAX_MESSAGE_SIZE = 1024 * 1024 * 16; //16MB
	public static final byte readChunkSize = 8;
	
	private byte[] message;
	private transient final Random rng;
	
	/**
	 * Constructors 
	 * @param in Byte array to wrap
	 * @param align True causes byte array to be multiple of {@link RSAMessage.readChunkSize}.
	 */
	public RSAMessage(byte[] in, boolean align){
		this.message = in;
		
		if (align) {
			byte bytesToAddInv;
			if ((bytesToAddInv = (byte) (this.message.length % (int) readChunkSize)) != 0) {
				byte[] zeros = new byte[readChunkSize - bytesToAddInv];
				ByteBuffer temp = (ByteBuffer) ByteBuffer.allocate(this.message.length	+ (readChunkSize - bytesToAddInv))
						.put(in).put(zeros);
				this.message = temp.array();
			}
		}
		this.rng = new Random(System.nanoTime());
	}
	
	public RSAMessage(String in, boolean align){
		this(in.getBytes(), true);
	}
	
	public RSAMessage(String in){
		this(in.getBytes());
	}
	
	public RSAMessage(){
		this("");
	}
	
	public RSAMessage(byte[] in){
		this(in, false);
	}
	
	/**
	 * Encrypt byte array with given public key.  Non mutable method.
	 * @param key Public key to use for encryption
	 * @return Encrypted Message
	 */
	public RSAMessage encryptMessage(KeyFile key){
		int count = 0, bytesWritten = 0; byte readChunkSizeLocal = RSAMessage.readChunkSize;
		ByteBuffer output = ByteBuffer.allocate(MAX_MESSAGE_SIZE); 
		
		if (this.message.length < readChunkSizeLocal){
			readChunkSizeLocal = (byte) this.message.length;
		}
		
		while(count < this.message.length){
			byte[] temp = Arrays.copyOfRange(this.message, count, count + readChunkSizeLocal); count += readChunkSizeLocal;
			
			//get random number
			BigInteger randNum = new BigInteger(readChunkSizeLocal * 8, this.rng);
			byte[] randNumBytes = randNum.toByteArray();
			byte[] randWrite = ByteBuffer.allocate(randNumBytes.length).put(randNumBytes).array();
			output.put((byte) randWrite.length); bytesWritten++;
			output.put(randWrite); bytesWritten += randWrite.length;
			
			// encrypt (xor with random number.  Send both result and random number)
			BigInteger data = new BigInteger(1, temp).xor(randNum).modPow(key.getKey(), key.getGroupSize());
			byte[] dataBytes = data.toByteArray();
			byte[] toWrite = ByteBuffer.allocate(dataBytes.length).put(data.toByteArray()).array();
			output.put((byte) toWrite.length); bytesWritten++;
			output.put(toWrite); bytesWritten += toWrite.length;
			
			if (count + readChunkSize > this.message.length){
				readChunkSizeLocal = (byte) (this.message.length - count);
			}
		}	
		
		// shrink byte array as necessary
		RSAMessage toRet = new RSAMessage(Arrays.copyOfRange(output.array(), 0, bytesWritten));
		return toRet;
	}
	
	public RSAMessage decryptMessage(KeyFile key){
		int bytesWritten = 0;
		ByteBuffer output = ByteBuffer.allocate(MAX_MESSAGE_SIZE); 
		ByteBuffer input = ByteBuffer.wrap(this.message);

		while(input.hasRemaining()){
			// read random number
			byte numBytes = input.get();
			if (numBytes == 0) break; // if empty stop trying to read;
			byte[] randIn = new byte[numBytes]; input.get(randIn);
			BigInteger randInBI = new BigInteger(1,randIn);

			// read encrypted number
			numBytes = input.get();
			byte[] dataIn = new byte[numBytes]; input.get(dataIn);
			BigInteger dataInBI = new BigInteger(dataIn).modPow(key.getKey(), key.getGroupSize()).xor(randInBI);		

			//trim appropriately
			byte[] sizeCheckTemp;
			if ((sizeCheckTemp = dataInBI.toByteArray()).length != readChunkSize){
				// strip leading zero if chunksize + 1
				if (sizeCheckTemp.length == readChunkSize + 1 && sizeCheckTemp[0] == 0) {
					sizeCheckTemp = Arrays.copyOfRange(sizeCheckTemp, 1, readChunkSize + 1);
					dataInBI = new BigInteger(sizeCheckTemp);			
				} 
				
				//strip trailing zeros
				byte nonZeroCount = 0; byte[] transferTemp;
				for (int i = 0; i < sizeCheckTemp.length; i++){
					if (sizeCheckTemp[i] != 0) nonZeroCount = (byte) i;
				}
				transferTemp = new byte[nonZeroCount + 1];
				for (int i = 0; i < transferTemp.length; i++){
					transferTemp[i] = sizeCheckTemp[i]; 
				}
				
				// prepare for next checks
				sizeCheckTemp = transferTemp;
				dataInBI = new BigInteger(sizeCheckTemp);

				//strip leading byte if zero
				if (sizeCheckTemp.length < readChunkSize){
					byte[] numZero = new byte[((int) readChunkSize - sizeCheckTemp.length)];
					byte[] newData = ByteBuffer.allocate(readChunkSize).put(sizeCheckTemp).put(numZero).array();
					dataInBI = new BigInteger(newData);
				}
			}
			
			//write next chunk to output buffer
			output.put(ByteBuffer.allocate(readChunkSize).put(dataInBI.toByteArray()).array());
			bytesWritten += readChunkSize;
		}

		RSAMessage toRet = new RSAMessage(Arrays.copyOfRange(output.array(), 0, bytesWritten));
		return toRet;
	}
	
	public byte[] getMessage(){
		return this.message;
	}
	
	public String toString(){
		return new String(this.message);
	}
}
