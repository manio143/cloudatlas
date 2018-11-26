package pl.edu.mimuw.cloudatlas.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

class ValueSerializer implements CustomSerializable<Value> {
    public static final byte marker = 0x03;

    private static final TypeSerializer ts = new TypeSerializer();

    public void serialize(OutputStream stream, Value value) throws IOException {
        stream.write(marker);
        Type t = value.getType();
        ts.serialize(stream, t);
        if (value.isNull()) {
            stream.write(0);
            return;
        }
        stream.write(1);

        DataOutputStream dataStream = new DataOutputStream(stream);

        if (t.getPrimaryType().equals(Type.PrimaryType.BOOLEAN)) {
            dataStream.writeBoolean(((ValueBoolean) value).getValue());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.CONTACT)) {
            ValueContact vc = (ValueContact)value;
            dataStream.writeUTF(vc.getName().getName());
            dataStream.write(vc.getAddress().getAddress());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.DOUBLE)) {
            dataStream.writeDouble(((ValueDouble) value).getValue());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.DURATION)) {
            dataStream.writeLong(((ValueDuration) value).getValue());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.INT)) {
            dataStream.writeLong(((ValueInt) value).getValue());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.TIME)) {
            dataStream.writeLong(((ValueTime) value).getValue());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.STRING)) {
            dataStream.writeUTF(((ValueString) value).getValue());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.LIST)) {
            dataStream.writeInt(((ValueList) value).size());
            for (Value v : ((ValueList) value).getValue())
                serialize(stream, v);
        } else if (t.getPrimaryType().equals(Type.PrimaryType.SET)) {
            dataStream.writeInt(((ValueSet) value).size());
            for (Value v : ((ValueSet) value).getValue())
                serialize(stream, v);
        }
    }

    public Value deserialize(InputStream stream) throws IOException {
        int rMarker = stream.read();
        if (rMarker != TypeSerializer.marker)
            throw new SerializationException("Invalid stream format: Type marker expected.");
        Type t = ts.deserialize(stream);

        boolean isNull = stream.read() == 0;

        DataInputStream dataStream = new DataInputStream(stream);
        if (t.getPrimaryType().equals(Type.PrimaryType.BOOLEAN)) {
            if (isNull) return new ValueBoolean(null);
            return new ValueBoolean(dataStream.readBoolean());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.CONTACT)) {
            if (isNull) return new ValueContact(null, null);
            PathName p = new PathName(dataStream.readUTF());
            byte[] address = new byte[4];
            stream.read(address);
            InetAddress ia = InetAddress.getByAddress(address);
            return new ValueContact(p, ia);
        } else if (t.getPrimaryType().equals(Type.PrimaryType.DOUBLE)) {
            if (isNull) return new ValueDouble(null);
            return new ValueDouble(dataStream.readDouble());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.DURATION)) {
            if (isNull) return new ValueDuration((Long)null);
            return new ValueDuration(dataStream.readLong());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.INT)) {
            if (isNull) return new ValueInt(null);
            return new ValueInt(dataStream.readLong());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.TIME)) {
            if (isNull) return new ValueTime((Long)null);
            return new ValueTime(dataStream.readLong());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.STRING)) {
            if (isNull) return new ValueString(null);
            return new ValueString(dataStream.readUTF());
        } else if (t.getPrimaryType().equals(Type.PrimaryType.LIST)) {
            if (isNull) return new ValueList(null);
            int size = dataStream.readInt();
            ValueList list = new ValueList(((TypeCollection)t).getElementType());
            for (int i = 0; i < size; i++) {
                int vMarker = stream.read();
                if (vMarker != marker)
                    throw new SerializationException("Invalid stream format: Value marker expected.");
                list.add(deserialize(stream));
            }
            return list;
        } else if (t.getPrimaryType().equals(Type.PrimaryType.SET)) {
            if (isNull)
                return new ValueSet(null);
            int size = dataStream.readInt();
            ValueSet set = new ValueSet(((TypeCollection)t).getElementType());
            for (int i = 0; i < size; i++) {
                int vMarker = stream.read();
                if (vMarker != marker)
                    throw new SerializationException("Invalid stream format: Value marker expected.");
                set.add(deserialize(stream));
            }
            return set;
        } else if (t.getPrimaryType().equals(Type.PrimaryType.NULL)) {
            return ValueNull.getInstance();
        }

        throw new SerializationException("Not yet implemented.");
    }
}