/*
 * NegoGUIApp.java
 */

package negotiator.gui;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;


/**
 * The main class of the application.
 */
public class NegoGUIApp extends SingleFrameApplication {
	public static NegoGUIView negoGUIView = null;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
    	negoGUIView = new NegoGUIView(this);
        show(negoGUIView); 
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of NegoGUIApp
     */
    public static NegoGUIApp getApplication() { 
        return Application.getInstance(NegoGUIApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(NegoGUIApp.class, args);
    }
    public static String getClassName()
    {
    	String thisClassName;

    	//	Build a string with executing class's name
    	thisClassName = NegoGUIApp.class.getName();
    	thisClassName = thisClassName.substring(thisClassName.lastIndexOf(".")
    			+ 1,thisClassName.length());
    	thisClassName += ".class"; //this is the name of the bytecode file that is executing

    	return thisClassName;
    } 
    public static String getLocalDirName()
    {
    	String localDirName;

    	//	Use that name to get a URL to the directory we are executing in
    	java.net.URL myURL = NegoGUIApp.class.getResource(getClassName());
    	//Open a URL to the our .class file

    	//Clean up the URL and make a String with absolute path name
    	localDirName = myURL.getPath(); //Strip path to URL object out
    	localDirName = myURL.getPath().replaceAll("%20", " "); //change %20 chars to spaces

    	//Get the current execution directory
    	localDirName =
    		localDirName.substring(0,localDirName.lastIndexOf("/")); //clean off the file name

    	return localDirName;
    } 
}
