package misc;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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
			
			if (log)
				is = new GZIPInputStream(is);

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
			
			if (log)
				os = new GZIPOutputStream(os);

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

	public String getFileName()
	{
		return fileName;
	}

}
