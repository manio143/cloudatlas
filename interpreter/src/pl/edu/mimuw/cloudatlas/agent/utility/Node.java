package pl.edu.mimuw.cloudatlas.agent.utility;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

import java.io.Serializable;

public class Node implements Serializable {
    public final PathName pathName;
    public final ValueTime freshness;
    public Node(PathName pathName, ValueTime freshness) {
        this.freshness = freshness;
        this.pathName = pathName;
    }
}
