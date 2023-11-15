package com.example.springmongo.converters;

import java.nio.ByteBuffer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FloatToByteConverter implements Converter<float[], byte[]> {

    @Override
    public byte[] convert(float[] source) {
        ByteBuffer bb = ByteBuffer.allocate(source.length * 4);
        for (float f : source) {
            bb.putFloat(f);
        }
        return bb.array();
    }
}
