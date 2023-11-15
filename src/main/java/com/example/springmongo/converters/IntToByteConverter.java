package com.example.springmongo.converters;

import java.nio.ByteBuffer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IntToByteConverter implements Converter<int[], byte[]> {

    @Override
    public byte[] convert(int[] source) {
        ByteBuffer bb = ByteBuffer.allocate(source.length * 4);
        for (int i : source) {
            bb.putInt(i);
        }
        return bb.array();
    }

}
