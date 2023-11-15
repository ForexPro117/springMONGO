package com.example.springmongo;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Gena {
    private Binary bin;

    private byte[] he;

    public byte[] getHe() {
        return he;
    }

    public void setHe(byte[] he) {
        this.he = he;
    }

    public Binary getBin() {
        return bin;
    }

    public void setBin(Binary bin) {
        this.bin = bin;
    }
}
