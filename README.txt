Setting up Eclipse for running/debugging NegotiationGUI

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
  * check the project isNegotiationGUI
  * enter "negotiator.gui.NegoGUIApp" as Main class
  * switch to the Arguments tab
  * in "Working Directory" enter "${workspace_loc:NegotiatorGUI/debug}" 
  * switch to JRE tab and check Java SE 7 is selected  
  

NOTICE!

The files in de debug folder are ONLY FOR DEBUGGING. 
Modifying these has no effect on the release version of Genius.

