package pl.edu.mimuw.cloudatlas.model.serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.edu.mimuw.cloudatlas.model.*;

public class SerializationInputStream extends DataInputStream {
    private static final ValueSerializer vs = new ValueSerializer();
    private static final TypeSerializer ts = new TypeSerializer();
    private static final AttributesMapSerializer ams = new AttributesMapSerializer();
    private static final ZMISerializer zs = new ZMISerializer();

    public SerializationInputStream(InputStream stream) {
        super(stream);
    }

    public Value readValue() throws IOException {
        int marker = read();
        if (marker != ValueSerializer.marker)
            throw new SerializationException("Invalid stream format: Value marker expected.");
        return vs.deserialize(this.in);
    }

    public Type readType() throws IOException {
        int marker = read();
        if (marker != TypeSerializer.marker)
            throw new SerializationException("Invalid stream format: Type marker expected.");
        return ts.deserialize(this.in);
    }

    public AttributesMap readAttributesMap() throws IOException {
        int marker = read();
        if (marker != AttributesMapSerializer.marker)
            throw new SerializationException("Invalid stream format: AttributesMap marker expected.");
        return ams.deserialize(this.in);
    }

    public ZMI readZMI() throws IOException {
        int marker = read();
        if (marker != ZMISerializer.marker)
            throw new SerializationException("Invalid stream format: ZMI marker expected.");
        return zs.deserialize(this.in);
    }
}