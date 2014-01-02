package shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;

/**
 * General utilities and standardizations for serializing and reading data
 * 
 * @author Chae Jubb
 * @version 1.2
 * 
 */
public final class Utilities {

	/**
	 * Serialize given object out to given file
	 * 
	 * @param obj
	 *          Object to serialize
	 * @param file
	 *          File to write object to
	 * @throws IOException
	 *           Writing to file
	 */
	public static void serializeToFile(Object obj, File file) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		out.close();
		fileOut.close();
	}

	/**
	 * Deserialize object from given file
	 * 
	 * @param file
	 *          File to deserialize from
	 * @return Deserialized object
	 * @throws IOException
	 *           Reading file
	 * @throws ClassNotFoundException
	 *           Should not be thrown as only casting to Object
	 */
	public static Object deserializeFromFile(File file) throws IOException, ClassNotFoundException{
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Object toRet = in.readObject();
		in.close();
		fileIn.close();

		return toRet;
	}

	/**
	 * Serialize given object out to byte array
	 * 
	 * @param obj
	 *          Object to serialize
	 * @return Serialized object as byte array
	 * @throws IOException
	 *           Writing to byteArray
	 */
	public static byte[] serializeToByteArray(Object obj) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	/**
	 * Deserialize object from byte array
	 * 
	 * @param data
	 *          Byte array to deserialize from
	 * @return Deserialized object
	 * @throws IOException
	 *           Converting byte array
	 * @throws ClassNotFoundException
	 *           Should not be thrown as only casting to Object
	 */
	public static Object deserializeFromByteArray(byte[] data) throws IOException,
			ClassNotFoundException{
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	/**
	 * Serialize to output stream
	 * 
	 * @param obj
	 *          Object to serialize
	 * @param out
	 *          Where to put serialized object
	 * @throws IOException
	 *           Writing to output stream
	 */
	public static void serialize(Object obj, OutputStream out) throws IOException{
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
	}

	/**
	 * Deserialize from input stream
	 * 
	 * @param in
	 *          Where to read serialized object from
	 * @return Deserialized object
	 * @throws IOException
	 *           Reading from input stream
	 * @throws ClassNotFoundException
	 *           Should not be thrown as only casting to Object
	 */
	public static Object deserialize(InputStream in) throws IOException, ClassNotFoundException{
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	/**
	 * Read one bytes from input stream.
	 * 
	 * @param in
	 *          Where to read bytes from
	 * @return Byte read
	 * @throws IOException
	 *           Reading from input stream
	 */
	public static byte receiveByte(InputStream in) throws IOException{
		byte data = -1;
		if ((data = (byte) in.read()) == (byte) -1) System.err.println("BUFF ERROR RECEIVE BYTE");
		return data;
	}

	/**
	 * Read byte array from input stream. First four bytes give number of following bytes as
	 * integer.
	 * 
	 * @param in
	 *          Where to read byte array from.
	 * @return Byte array read
	 * @throws IOException
	 *           Reading from input stream
	 */
	public static byte[] receiveData(InputStream in) throws IOException{
		byte[] size = new byte[4];
		if (in.read(size, 0, 4) != 4) System.err.println("BUFF ERROR RECEIVE DATA MASS");
		ByteBuffer temp = ByteBuffer.allocate(4).put(size);
		temp.flip();
		int dataSize = temp.getInt();

		byte[] toRet = new byte[dataSize];
		in.read(toRet);
		return toRet;
	}

	/**
	 * Write byte to given output stream
	 * 
	 * @param data
	 *          Byte to write
	 * @param out
	 *          Where to write byte
	 * @throws IOException
	 *           Writing to output stream
	 */
	public static void sendByte(byte data, OutputStream out) throws IOException{
		out.write(data);
	}

	/**
	 * Write byte array to given output stream. First four bytes indicate size of following
	 * byte array.
	 * 
	 * @param data
	 *          Byte array to write
	 * @param out
	 *          Where to write bytes
	 * @throws IOException
	 *           Writing to output stream
	 */
	public static void sendData(byte[] data, OutputStream out) throws IOException{
		byte[] dataLength = ByteBuffer.allocate(4).putInt(data.length).array();
		out.write(dataLength);
		out.write(data);
	}

	/**
	 * @return Timestamp of current time
	 */
	public static Timestamp getTimeStamp(){
		return new Timestamp((new Date()).getTime());
	}
}
