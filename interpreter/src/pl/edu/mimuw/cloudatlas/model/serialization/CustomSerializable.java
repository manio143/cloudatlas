package pl.edu.mimuw.cloudatlas.model.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface CustomSerializable<T> {
    void serialize(OutputStream stream, T value) throws IOException;

    T deserialize(InputStream stream) throws IOException;
}