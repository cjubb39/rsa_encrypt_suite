package shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * General utilities and standardizations for serializing and reading data
 * @author Chae Jubb
 * @version 1.0
 *
 */
public final class Utilities {

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
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

	public static byte[] receieveData(InputStream in) throws IOException {
		byte[] size = new byte[4];
		if (in.read(size, 0, 4) != 4)
			System.err.println("BUFF ERROR RECEIVEDATA");
		ByteBuffer temp = ByteBuffer.allocate(4).put(size);
		temp.flip();
		int dataSize = temp.getInt();

		byte[] toRet = new byte[dataSize];
		in.read(toRet);
		return toRet;
	}

	public static void sendData(byte[] data, OutputStream out) throws IOException {
		byte[] dataLength = ByteBuffer.allocate(4).putInt(data.length).array();
		out.write(dataLength);
		out.write(data);
	}
}
