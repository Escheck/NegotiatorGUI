Project setup
=============

The NegotiatorGUI project mainly is to wrap together different components for a distribution.
The different components that Genius consists of are in the following separate projects:

|=========|================================================|
| project | description                                    |
|=========|================================================|
| core    | the actual genius core code                    |
| agents  | example agents that are included with Genius   |
| domains | the various example domains coming with Genius |
|=========|================================================|

You can access all these projects with your SVN account at

http://ii.tudelft.nl/trac/negotiation/<project>

Most projects generate multiple jars in their target directory. All these jars are copied
into the dep/ directory of the NegotiatorGUI project.

If you make changes in one of the projects, you have to 
 * build the changed project (ant run the build.xml).
 * copy all target/*.jar files into NegotiatorGUI/dep/


Debugging with Eclipse
======================
Setting up Eclipse for running/debugging NegotiationGUI goes as follows

1. Run build.xml with the target "debug":
  * right click on build
  * run as/"Ant Build..."
  * check only 'debug'
  * run the script
2. Create run script:
  * select the NegotiationGUI project in package explorer/navigator
  * click on debug icon down arrow to open the options
  * click "Debug Configurations..."
  * click "new launch configuration" (the icon in top left)
  * Switch to Main tab
  * check the project is NegotiationGUI
  * enter "negotiator.gui.NegoGUIApp" as Main class
  * switch to the Arguments tab
  * in "Working Directory" enter "${workspace_loc:NegotiatorGUI/debug}" 
  * switch to JRE tab and check Java SE 7 is selected  
  

NOTICE!

The files in de debug folder are ONLY FOR DEBUGGING. 
Modifying these has no effect on the release version of Genius.

