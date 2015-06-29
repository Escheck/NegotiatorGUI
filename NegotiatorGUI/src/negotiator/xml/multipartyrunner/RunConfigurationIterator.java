package negotiator.xml.multipartyrunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunConfigurationIterator implements Iterator<RunConfiguration> {

    private List<RunConfiguration> configs;
    private int totalRepetitions;
    private boolean usesPermutations;

    private RunConfiguration currentConfig = null;
    private List<RunConfiguration> permutations = new ArrayList<RunConfiguration>();
    private int repetitionsLeft = 0;

    private boolean hasNext = true;

    public RunConfigurationIterator(XmlObject xmlObject) {
        configs = xmlObject.getRunConfigurations();
        totalRepetitions = xmlObject.getRepetitions();
        usesPermutations = xmlObject.getPermutationFlag();
    }

    @Override
    public boolean hasNext() {
        return !configs.isEmpty() || !permutations.isEmpty() || repetitionsLeft > 0;
    }

    @Override
    public RunConfiguration next() {
        // guard, if no next exists, return null;
        if (!hasNext()) return null;

        /*
         triple loop:
           1) configurations (different entries in xml)
           2) permutations   (different permutations of entries)
           3) repetitions    (repetitions of this entries)
         */
        if (usesPermutations && permutations.isEmpty() && !configs.isEmpty()) {
            permutations = configs.remove(0).generatePermutations();
        }

        if (repetitionsLeft == 0) {
            currentConfig = usesPermutations ? permutations.remove(0) : configs.remove(0);
            repetitionsLeft = totalRepetitions;
        }

        repetitionsLeft--;
        return currentConfig;
    }

    @Override
    public void remove() {
        next();
    }
}
