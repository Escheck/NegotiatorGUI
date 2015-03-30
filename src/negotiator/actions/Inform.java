package negotiator.actions;

/**
 * Created by dfesten on 21-8-2014.
 */
public class Inform extends Action {
    private Object value;
    private String name;

    public Inform() {
    }

    public Inform(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public Inform setValue(Object information) {
        this.value = information;
        return this;
    }

    public String getName() {
        return name;
    }

    public Inform setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Enforces that actions implements a string-representation.
     */
    @Override
    public String toString() {
        return name + ":" + value.toString();
    }
}
