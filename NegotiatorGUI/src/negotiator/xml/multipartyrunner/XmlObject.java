package negotiator.xml.multipartyrunner;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "negotiation-runner")
class XmlObject {

    public List<RunConfiguration> getRunConfigurations() {
        return runConfigurations;
    }

    public int getRepetitions() {
        return repetitions;
    }
    @XmlElement(name = "configuration")
    private List<RunConfiguration> runConfigurations;


    @XmlElement(name = "repetitions")
    private int repetitions = 1;

}
