package com.example.springmongo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Document(collection = "geometryData")
public class MongoGeometryData extends EntityUuid {

    private String hashCode;
    private int[] indices;
    private double[] vertices;
    private float[] normals;
    private int[] colorsQuantized;
    private float[] colors;


    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public double[] getVertices() {
        return vertices;
    }

    public void setVertices(double[] vertices) {
        this.vertices = vertices;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public int[] getColorsQuantized() {
        return colorsQuantized;
    }

    public void setColorsQuantized(int[] colorsQuantized) {
        this.colorsQuantized = colorsQuantized;
    }

    public float[] getColors() {
        return colors;
    }

    public void setColors(float[] colors) {
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
