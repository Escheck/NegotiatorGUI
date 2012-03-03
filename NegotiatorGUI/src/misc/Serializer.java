package misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Serializer<A>
{
	private final String fileName;
	private final String classDescription;
	private final boolean log; 
	
	public Serializer(String fileName)
	{
		this(fileName, "");
	}

	public Serializer(String fileName, String description)
	{
		this(fileName, description, false);
	}
	
	public Serializer(String fileName, String description, boolean log)
	{
		super();
		this.fileName = fileName;
		this.classDescription = description;
		this.log = log;
	}

	public A readFromDisk()
	{
		InputStream is = null;
		ObjectInputStream ois = null;
		A obj = null;

		final String classMsg = "".equals(classDescription) ? "" : " in " + classDescription;
		final String errorMsg = "Error opening ("+fileName+")" + classMsg + ":\n";
		try
		{
			is = new BufferedInputStream(new FileInputStream(fileName), 50000 * 1024);
			
			ois = new ObjectInputStream(is);
				
			final Object readObject = ois.readObject();
			ois.close();
			is.close();
			obj = (A) readObject;
			return obj;
		} catch (FileNotFoundException e)
		{
			if (log)
				System.out.println(errorMsg + e);
		} catch (IOException e)
		{
			System.out.println(errorMsg + e);

		} catch (ClassNotFoundException e)
		{
			System.out.println(errorMsg + e);
		}
			catch (ClassCastException e)
		{
			System.out.println(errorMsg + e);
		}
		System.out.println(fileName + " is old " + classMsg + "; It should be rebuilt.");
		return null;
	}


	
	public void writeToDisk(A a)
	{
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try
		{
			os = new BufferedOutputStream(new FileOutputStream(fileName));
			
			oos = new ObjectOutputStream(os);
			oos.writeObject(a);
			oos.close();
			os.close();
			System.out.println(classDescription + " written.");
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Serializes an object to a string encoded by using Base64 to
	 * avoid characterset problems.
	 * 
	 * @param a object to serialize
	 * @return serialized object
	 */
	public String writeToString(A a) {
		BASE64Encoder encode = new BASE64Encoder();

		String out = null;
		if (a != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(a);
				out = encode.encode(baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return out;
	}
	
	/**
	 * Converts a string back to an object.
	 * 
	 * @param str serialized object
	 * @return unserialized object
	 */
	public A readStringToObject(String str) {
		BASE64Decoder decode = new BASE64Decoder();

		Object out = null;
		if (str != null) {
			try {
				ByteArrayInputStream bios = new ByteArrayInputStream(decode.decodeBuffer(str));
				ObjectInputStream ois = new ObjectInputStream(bios);
				out = ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return (A)out;
	}

	public String getFileName() {
		return fileName;
	}
}
