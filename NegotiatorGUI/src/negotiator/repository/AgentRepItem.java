package negotiator.repository;

import negotiator.exceptions.Warning;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.net.URL;
import java.io.File;

/**
 * This repository item contains all info about an agent that can be loaded.

 * 
 * @author wouter
 *
 */
public class AgentRepItem implements RepItem
{
	
	String agentName; /**  the key: unique name of the agent as it will be known in the nego system.
 * This is an arbitrary but unique label. */
	String classPath; /** file path including the class name */
	String version; /** the version, should match the version as specified by the agent */
	String description; /** description of this agent */
	
	public AgentRepItem(String aName, String cPath, String desc) {
		agentName=aName; 
		classPath=cPath;
		description=desc;
	}
	
	public String getName() { return agentName; }
	public String getPath() { return classPath; }
	
	/** getVersion is bit involved, need to call the agent getVersion() to get it */
	private static final Class[] parameters = new Class[]{URL.class};
	public String getVersion() { 
	       try{
	    	   /*
	    	   // following code somewhere from the net, see ClassPathHacker 
	   		URLClassLoader sysloader=(URLClassLoader)ClassLoader.getSystemClassLoader();
	   		Class sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			URL urloffile=new File(classPath).toURL();
			method.invoke(sysloader,new Object[]{ urloffile }); // load the new class.
			*/
			Class c=Class.forName(classPath);
			return ""+c.getMethod("getVersion").invoke(null, new Object[0]);
			//Class agentClass=classLoader.loadClass(classPath);
	        //  return ""+agentClass.getMethod("getVersion").invoke(null, new Object[0]);
			
	       } catch(Exception e){
	           new Warning("can't get version for "+agentName+":"+e); e.printStackTrace();
	       }  		
	       return "ERR";
		}
	
	public String getDescription() { return description; }
}