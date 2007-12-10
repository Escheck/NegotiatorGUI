Tournament
W.Pasman, 16 dec 2007


This is a brief manual on how to create and run a tournament for the Negotiator system.


Overview
There are a number of steps involved in running the tournament. 
-1. Throw away the old tournamentxml file. Not strictly necessary but to avoid lot of rubbish.
0. Unzip a standard negotiator distribution file with all the needed agents, e.g. the 21nov version. 
	This directory is called the NEGODIR.
1. Edit the setting files.
2. run the tournament builder.
3. Copy the run_tournament.bat file  and tournamentxml directory entirely to NEGODIR.
4. Copy all the agent directories as specified in the competition.xml file to the NEGODIR
4. Copy the outcomes.xsl file to the NEGODIR
4. Copy the utility directory to the NEGODIR/tournamentxml directory. NOTE, in tournamentxml!!!! (bug in java???)

4. Start a shell 
5. Go to the negotiator distribution directory
6. execute the run_tournament.bat file.
7. open results



----------------------------DETAILS--------------------------

1. Edit the setting files.
Edit the competition.xml file in the src_TournabemtBuilder/tournamentbuilder directory. It contains the group names and their agent class file



create a file eg. party.xml in the tournamentbuilder directiry.
adjust the Main.NEGOTEMPLATE variable to the name of the file, without the ".xml".

Your util files should bear the names <NEGOTEMPLATE>_<XX>_utility.xml and be contained in a folder named "utility"
e.g. utility/party_11_utility.xml
with XX an continuous range from LOW_UTIL_NR to HIGH_UTIL_NR.
adjust the Main.LOW_UTIL_NR and Main.HIGH_UTIL_NR variables


Use the names @agentA and @agentB for the names of agent a and b.
Use the names @agentClassA and agentClassB for the class names of agent a and b.
Use the names @utilityA and @utilityB for the utility filenames of agent a and b.


2. run the application
select src_TournamentBuilder/tournamentbuilder/Main.java application.
press the run button. 
This creates a run_tournament.bat file in the root of the NegotiatorGUI, and a new directory etc/templates/tournament.
If you don't see them, sleect the encapsulating folder and press F5.



6. run the .bat file
cd NEGODIR
chmod u+x run_tournament.bat
./run_tournament.bat

7.open results
dump the tournmentresult.xml file into Firefox or interenet explorer.

