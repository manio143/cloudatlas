package pl.edu.mimuw.cloudatlas.model.serialization;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ZMISerializer implements CustomSerializable<ZMI> {
    public static final byte marker = 0x01;

    private static final AttributesMapSerializer ams = new AttributesMapSerializer();

    public void serialize(OutputStream stream, ZMI value) throws IOException {
        stream.write(marker);
        ams.serialize(stream, value.getAttributes());
        new DataOutputStream(stream).writeInt(value.getSons().size());
        for (ZMI son : value.getSons())
            serialize(stream, son);
    }

    public ZMI deserialize(InputStream stream) throws IOException {
        int amMarker = stream.read();
        if (amMarker != AttributesMapSerializer.marker)
            throw new SerializationException("Invalid stream format: AttributesMap marker expected.");
        AttributesMap am = ams.deserialize(stream);
        int size = new DataInputStream(stream).readInt();
        ZMI r = new ZMI();
        r.getAttributes().add(am);
        for (int i = 0; i < size; i++) {
            int zMarker = stream.read();
            if (zMarker != marker)
                throw new SerializationException("Invalid stream format: ZMI marker expected.");
            ZMI s = deserialize(stream);
            s.setFather(r);
            r.addSon(s);
        }
        return r;
    }

}