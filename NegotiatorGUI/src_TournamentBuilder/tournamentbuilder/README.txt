Tournament
W.Pasman, 16 dec 2007


This is a brief manual on how to create and run a tournament for the Negotiator system.


Overview
There are a number of steps involved in running the tournament. 
1. Edit the competition.xml file in the src_TournabemtBuilder/tournamentbuilder directory. It contains the group names and their agent class file
2. run the src_TournamentBuilder/tournamentbuilder/Main.java application. This creates a run_tournament.bat file in the root of the NegotiatorGUI, and a new directory etc/templates/tournament.



create a file eg. party.xml in the tournamentbuilder directiry.
replace the name of agentA with "@agentA", idem agntB.