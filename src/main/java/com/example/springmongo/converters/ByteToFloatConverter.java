package com.example.springmongo.converters;

import java.nio.ByteBuffer;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ByteToFloatConverter implements Converter<Binary, float[]> {
    @Override
    public float[] convert(Binary source) {
        ByteBuffer bb = ByteBuffer.wrap(source.getData());
        float[] floats = new float[source.getData().length / 4];
        for(int i = 0; i < floats.length; i++) {
            floats[i] = bb.getFloat();
        }
        return floats;
    }
}
