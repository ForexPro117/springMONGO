package com.example.springmongo.converters;

import java.nio.ByteBuffer;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ByteToIntConverter implements Converter<Binary, int[]> {
    @Override
    public int[] convert(Binary source) {
        ByteBuffer bb = ByteBuffer.wrap(source.getData());
        int[] ints = new int[source.getData().length / 4];
        for(int i = 0; i < ints.length; i++) {
            ints[i] = bb.getInt();
        }
        return ints;
    }
}
