package com.example.springmongo;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "geometry_data")
public class GDByteBuffer {
    @PrimaryKey
    private UUID uuid;
    @Column(value = "hash_code")
    private String hashCode;
    private ByteBuffer indices;
    private ByteBuffer vertices;
    private ByteBuffer normals;
    @Column(value = "colors_quantized")
    private ByteBuffer colorsQuantized;
    private ByteBuffer colors;

    public GDByteBuffer() {}

    public GDByteBuffer(CassandraGeometryData data) {
        this.uuid = data.getUuid();
        this.hashCode = data.getHashCode();
        this.vertices = getByteArray(data.getVertices());
        this.normals = getByteArray(data.getNormals());
        this.colors = getByteArray(data.getColors());
        this.colorsQuantized = getByteArray(data.getColorsQuantized());
        this.indices = getByteArray(data.getIndices());
    }

    public CassandraGeometryData getGeometryData() {
        CassandraGeometryData data = new CassandraGeometryData();
        data.setUuid(this.uuid);
        data.setHashCode(this.hashCode);
        data.setColors(getFloatArray(this.colors));
        data.setColorsQuantized(getIntArray(this.colorsQuantized));
        data.setIndices(getIntArray(this.indices));
        data.setNormals(getFloatArray(this.normals));
        data.setVertices(getDoubleArray(this.vertices));

        return data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public ByteBuffer getIndices() {
        return indices;
    }

    public void setIndices(ByteBuffer indices) {
        this.indices = indices;
    }

    public ByteBuffer getVertices() {
        return vertices;
    }

    public void setVertices(ByteBuffer vertices) {
        this.vertices = vertices;
    }

    public ByteBuffer getNormals() {
        return normals;
    }

    public void setNormals(ByteBuffer normals) {
        this.normals = normals;
    }

    public ByteBuffer getColorsQuantized() {
        return colorsQuantized;
    }

    public void setColorsQuantized(ByteBuffer colorsQuantized) {
        this.colorsQuantized = colorsQuantized;
    }

    public ByteBuffer getColors() {
        return colors;
    }

    public void setColors(ByteBuffer colors) {
        this.colors = colors;
    }

    private ByteBuffer getByteArray(double[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 8);
        for (double d : arr) {
            bb.putDouble(d);
        }
        return bb;
    }

    private ByteBuffer getByteArray(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (float d : arr) {
            bb.putFloat(d);
        }
        return bb;
    }

    private ByteBuffer getByteArray(int[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (int d : arr) {
            bb.putInt(d);
        }
        return bb;
    }

    private int[] getIntArray(ByteBuffer arr) {
        var array = arr.array();
        ByteBuffer bb = ByteBuffer.wrap(array);
        int[] ints = new int[array.length / 4];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = bb.getInt();
        }
        return ints;
    }

    private float[] getFloatArray(ByteBuffer arr) {
        var array = arr.array();
        ByteBuffer bb = ByteBuffer.wrap(array);
        float[] floats = new float[array.length / 4];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = bb.getFloat();
        }
        return floats;
    }

    private double[] getDoubleArray(ByteBuffer arr) {
        var array = arr.array();
        ByteBuffer bb = ByteBuffer.wrap(array);
        double[] doubles = new double[array.length / 8];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = bb.getDouble();
        }
        return doubles;
    }
}
