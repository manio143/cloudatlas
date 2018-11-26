package pl.edu.mimuw.cloudatlas.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class TypeSerializer implements CustomSerializable<Type> {
    public static final byte marker = 0x02;

    public void serialize(OutputStream stream, Type value) throws IOException {
        stream.write(marker);
        stream.write(primaryTypeValue(value.getPrimaryType()));
        if (value.isCollection())
            serialize(stream, ((TypeCollection) value).getElementType());
    }

    public Type deserialize(InputStream stream) throws IOException {
        // we assume marker has been read and thus this method has been called
        Type.PrimaryType p = primaryTypeFromValue(stream.read());
        if (p.equals(Type.PrimaryType.LIST) || p.equals(Type.PrimaryType.SET)) {
            int rMarker = stream.read();
            if (rMarker != marker)
                throw new SerializationException("Invalid stream format: Type marker expected.");
            Type e = deserialize(stream);
            return new TypeCollection(p, e);
        }
        return typePrimitiveFromPrimaryType(p);
    }

    int primaryTypeValue(Type.PrimaryType p) {
        if (p.equals(Type.PrimaryType.BOOLEAN))
            return 1;
        if (p.equals(Type.PrimaryType.CONTACT))
            return 2;
        if (p.equals(Type.PrimaryType.DOUBLE))
            return 3;
        if (p.equals(Type.PrimaryType.DURATION))
            return 4;
        if (p.equals(Type.PrimaryType.INT))
            return 5;
        if (p.equals(Type.PrimaryType.LIST))
            return 6;
        if (p.equals(Type.PrimaryType.NULL))
            return 7;
        if (p.equals(Type.PrimaryType.SET))
            return 8;
        if (p.equals(Type.PrimaryType.STRING))
            return 9;
        if (p.equals(Type.PrimaryType.TIME))
            return 10;
        throw new IllegalArgumentException("PrimitiveType out of range");
    }

    Type.PrimaryType primaryTypeFromValue(int v) {
        switch (v) {
        case 1:
            return Type.PrimaryType.BOOLEAN;
        case 2:
            return Type.PrimaryType.CONTACT;
        case 3:
            return Type.PrimaryType.DOUBLE;
        case 4:
            return Type.PrimaryType.DURATION;
        case 5:
            return Type.PrimaryType.INT;
        case 6:
            return Type.PrimaryType.LIST;
        case 7:
            return Type.PrimaryType.NULL;
        case 8:
            return Type.PrimaryType.SET;
        case 9:
            return Type.PrimaryType.STRING;
        case 10:
            return Type.PrimaryType.TIME;
        default:
            throw new IllegalArgumentException("PrimaryType value not in range.");
        }
    }

    Type typePrimitiveFromPrimaryType(Type.PrimaryType p) {
        if (p.equals(Type.PrimaryType.BOOLEAN))
            return TypePrimitive.BOOLEAN;
        if (p.equals(Type.PrimaryType.CONTACT))
            return TypePrimitive.CONTACT;
        if (p.equals(Type.PrimaryType.DOUBLE))
            return TypePrimitive.DOUBLE;
        if (p.equals(Type.PrimaryType.DURATION))
            return TypePrimitive.DURATION;
        if (p.equals(Type.PrimaryType.INT))
            return TypePrimitive.INTEGER;
        if (p.equals(Type.PrimaryType.NULL))
            return TypePrimitive.NULL;
        if (p.equals(Type.PrimaryType.STRING))
            return TypePrimitive.STRING;
        if (p.equals(Type.PrimaryType.TIME))
            return TypePrimitive.TIME;
        throw new IllegalArgumentException("PrimitiveType out of range");
    }
}