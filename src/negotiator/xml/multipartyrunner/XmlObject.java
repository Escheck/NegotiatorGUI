package negotiator.xml.multipartyrunner;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;

@XmlRootElement(name = "negotiation-runner")
class XmlObject implements Iterable<RunConfiguration> {

    public List<RunConfiguration> getRunConfigurations() {
        return runConfigurations;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public boolean getPermutationFlag() {
        return permutations;
    }

    @XmlElement(name = "configuration")
    private List<RunConfiguration> runConfigurations;

    @XmlElement(name = "repetitions")
    private int repetitions = 1;

    @XmlElement(name = "permutations")
    private boolean permutations = false;

    @Override
    public Iterator<RunConfiguration> iterator() {
        return new RunConfigurationIterator(this);
    }

    public int getNumberOfRuns() {
        if (permutations) {
            int numberRuns = 0;
            for (RunConfiguration runConfiguration : getRunConfigurations()) {
                numberRuns += runConfiguration.generatePermutations().size() * getRepetitions();
            }
            return numberRuns;
        } else {
            return getRunConfigurations().size() * getRepetitions();
        }
    }
}
