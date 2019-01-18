package pl.edu.mimuw.cloudatlas.model.serialization;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SerializationOutputStream extends DataOutputStream {
    public static final ValueSerializer vs = new ValueSerializer();
    public static final TypeSerializer ts = new TypeSerializer();
    public static final AttributesMapSerializer ams = new AttributesMapSerializer();
    public static final ZMISerializer zs = new ZMISerializer();

    public SerializationOutputStream(OutputStream stream) {
        super(stream);
    }

    public void writeValue(Value value) throws IOException {
        vs.serialize(this.out, value);
    }

    public void writeType(Type type) throws IOException {
        ts.serialize(this.out, type);
    }

    public void writeAttributesMap(AttributesMap am) throws IOException {
        ams.serialize(this.out, am);
    }

    public void writeZMI(ZMI zmi) throws IOException {
        zs.serialize(this.out, zmi);
    }
}