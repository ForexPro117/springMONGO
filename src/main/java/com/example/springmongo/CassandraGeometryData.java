package com.example.springmongo;


import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

@Table(value = "geometry_data")
public class CassandraGeometryData {

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

//    public String calcHash() {
//        StringBuffer hashCode = new StringBuffer();
//        if (getIndices() != null) {
//            hashCode.append(Arrays.hashCode(getIndices()));
//        }
//        if (getVertices() != null) {
//            hashCode.append("_").append(Arrays.hashCode(getVertices()));
//        }
//        if (getNormals() != null) {
//            hashCode.append("_").append(Arrays.hashCode(getNormals()));
//        }
//        if (getColorsQuantized() != null) {
//            hashCode.append("_").append(Arrays.hashCode(getColorsQuantized()));
//        }
//        if (getColors() != null) {
//            hashCode.append("_").append(Arrays.hashCode(getColors()));
//        }
//        return hashCode.toString();
//    }
}
