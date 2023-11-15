package com.example.springmongo.converters;

import java.nio.ByteBuffer;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ByteToDoubleConverter implements Converter<Binary, double[]> {
    @Override
    public double[] convert(Binary source) {
        ByteBuffer bb = ByteBuffer.wrap(source.getData());
        double[] doubles = new double[source.getData().length / 8];
        for(int i = 0; i < doubles.length; i++) {
            doubles[i] = bb.getDouble();
        }
        return doubles;
    }
}