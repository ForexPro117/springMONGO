package com.example.springmongo.geometrydata;

import com.example.springmongo.geometrydata.EntityUuid;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Document(collection = "geometryData")
public class MongoGeometryDataBin extends EntityUuid {

    private String hashCode;
    private byte[] indices;
    private byte[] vertices;
    private byte[] normals;
    private byte[] colorsQuantized;
    private byte[] colors;


    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public byte[] getIndices() {
        return indices;
    }

    public void setIndices(byte[] indices) {
        this.indices = indices;
    }

    public byte[] getVertices() {
        return vertices;
    }

    public void setVertices(byte[] vertices) {
        this.vertices = vertices;
    }

    public byte[] getNormals() {
        return normals;
    }

    public void setNormals(byte[] normals) {
        this.normals = normals;
    }

    public byte[] getColorsQuantized() {
        return colorsQuantized;
    }

    public void setColorsQuantized(byte[] colorsQuantized) {
        this.colorsQuantized = colorsQuantized;
    }

    public byte[] getColors() {
        return colors;
    }

    public void setColors(byte[] colors) {
        this.colors = colors;
    }

    public String calcHash() {
        StringBuffer hashCode = new StringBuffer();
        if (getIndices() != null) {
            hashCode.append(Arrays.hashCode(getIndices()));
        }
        if (getVertices() != null) {
            hashCode.append("_").append(Arrays.hashCode(getVertices()));
        }
        if (getNormals() != null) {
            hashCode.append("_").append(Arrays.hashCode(getNormals()));
        }
        if (getColorsQuantized() != null) {
            hashCode.append("_").append(Arrays.hashCode(getColorsQuantized()));
        }
        if (getColors() != null) {
            hashCode.append("_").append(Arrays.hashCode(getColors()));
        }
        return hashCode.toString();
    }
}
