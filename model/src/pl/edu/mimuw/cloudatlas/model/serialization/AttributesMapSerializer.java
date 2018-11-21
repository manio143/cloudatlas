package pl.edu.mimuw.cloudatlas.model.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import pl.edu.mimuw.cloudatlas.model.*;

class AttributesMapSerializer implements Serializable<AttributesMap> {
    public static final byte marker = 0x04;

    private static final ValueSerializer vs = new ValueSerializer();

    public void serialize(OutputStream stream, AttributesMap value) throws IOException {
        stream.write(marker);
        DataOutputStream dataStream = new DataOutputStream(stream);
        dataStream.writeInt(value.size());
        for (Entry<Attribute, Value> av : value) {
            dataStream.writeUTF(av.getKey().getName());
            vs.serialize(stream, av.getValue());
        }
    }

    public AttributesMap deserialize(InputStream stream) throws IOException {
        DataInputStream dataStream = new DataInputStream(stream);
        int size = dataStream.readInt();
        AttributesMap am = new AttributesMap();
        for (int i = 0; i < size; i++) {
            String attr = dataStream.readUTF();
            int vMarker = stream.read();
            if (vMarker != ValueSerializer.marker)
                throw new SerializationException("Invalid stream format: Value marker expected.");
            Value val = vs.deserialize(stream);
            am.add(attr, val);
        }
        return am;
    }

}