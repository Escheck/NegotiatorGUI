package misc;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MemoryLogger extends Thread
{
	@Override
	public void run()
	{
		while(true)
		{
			logFreeMemory("");
			try
			{
				Thread.sleep(10000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void logFreeMemory(String msg) 
	{
		garbageCollect();
		long freeMemory = Runtime.getRuntime().freeMemory();
		String fM =Math.round((double) freeMemory / 1024 / 1024)+" mb";
		log("Free memory; "+freeMemory + "  (= "+fM+"). " + msg);
	}

	public static void garbageCollect() 
	{
		System.gc();System.gc();System.gc();System.gc();
	}

	public static void log(String msg)
	{
		String prefix = createTimeStamp();
		System.out.println(prefix + " " + msg);
	}

	public final static String createTimeStamp()
	{
		Calendar c= new GregorianCalendar();
		String hour = ""+c.get(Calendar.HOUR_OF_DAY);
		String minute = ""+c.get(Calendar.MINUTE);
		String second = ""+c.get(Calendar.SECOND);
		String msecond = ""+c.get(Calendar.MILLISECOND);

		if (hour.length() == 1) hour = "0"+hour;
		if (minute.length() == 1) minute= "0"+minute;
		if (second.length() == 1) second= "0"+second;
		if (msecond.length() == 1) msecond= "00"+msecond;
		if (msecond.length() == 2) msecond= "0"+msecond;
		return 	hour+":"+minute+":"+second+":"+msecond;
	}
}

