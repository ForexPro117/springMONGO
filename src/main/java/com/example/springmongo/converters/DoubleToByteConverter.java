package com.example.springmongo.converters;


import java.nio.ByteBuffer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DoubleToByteConverter implements Converter<double[], byte[]> {

    @Override
    public byte[] convert(double[] source) {
        ByteBuffer bb = ByteBuffer.allocate(source.length * 8);
        for (double d : source) {
            bb.putDouble(d);
        }
        return bb.array();
    }
}
