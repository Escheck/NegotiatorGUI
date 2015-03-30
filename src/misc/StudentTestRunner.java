package misc;

import negotiator.session.TournamentManager;

public class StudentTestRunner {

    public static final int TIME_DEADLINE = 30;

    public static void main(String[] args) {

        int amountOfScenarios = StudentConfiguration.getAllPartyProfileItems().size() / 3;

        for (int i = 0; i < amountOfScenarios; i++) {
            TournamentManager tournamentManager = new TournamentManager(new StudentConfiguration(i));
            System.out.println("Loaded custom tournament " + (i + 1) + "/" + amountOfScenarios);
            tournamentManager.run();
        }
    }
}
