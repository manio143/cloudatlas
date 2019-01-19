package pl.edu.mimuw.cloudatlas.agent.utility;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

import java.io.Serializable;
import java.util.List;

public class Sibling implements Serializable {
    public final PathName pathName;
    public final List<ValueContact> contacts;
    public final ValueTime timestamp;
    public Sibling(PathName pathName, List<ValueContact> contacts, ValueTime timestamp) {
        this.pathName = pathName;
        this.contacts = contacts;
        this.timestamp = timestamp;
    }
}
