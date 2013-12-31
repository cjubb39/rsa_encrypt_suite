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

/**
 * General utilities and standardizations for serializing and reading data
 * @author Chae Jubb
 * @version 1.1
 *
 */
public final class Utilities {

	public static void serializeToFile(Object obj, File file) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		out.close();
		fileOut.close();
	}
	
	public static Object deserializeFromFile(File file) throws IOException, ClassNotFoundException{
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Object toRet = in.readObject();
		in.close();
		fileIn.close();
		
		return toRet;
	}
	
	public static byte[] serializeToByteArray(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserializeFromByteArray(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
	
	public static void serialize(Object obj, OutputStream out) throws IOException {
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
	}
	
	public static Object deserialize(InputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	public static byte receiveByte(InputStream in) throws IOException{
		byte data = -1;
		if ((data = (byte) in.read()) == (byte) -1)
			System.err.println("BUFF ERROR RECEIVE BYTE");
		return data;
	}

	public static byte[] receiveData(InputStream in) throws IOException {
		byte[] size = new byte[4];
		if (in.read(size, 0, 4) != 4)
			System.err.println("BUFF ERROR RECEIVE DATA MASS");
		ByteBuffer temp = ByteBuffer.allocate(4).put(size);
		temp.flip();
		int dataSize = temp.getInt();

		byte[] toRet = new byte[dataSize];
		in.read(toRet);
		return toRet;
	}

	public static void sendByte(byte data, OutputStream out) throws IOException {
		out.write(data);
	}

	public static void sendData(byte[] data, OutputStream out) throws IOException {
		byte[] dataLength = ByteBuffer.allocate(4).putInt(data.length).array();
		out.write(dataLength);
		out.write(data);
	}
}
