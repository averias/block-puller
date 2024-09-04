package com.averiasconnect.blockpuller.external.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {
    public String hash;
    public String number;
    public String parentHash;
    public String timestamp;
    public ArrayList<Transaction> transactions;
    public String network;

    public Long getBlockNumberAsLong() {
        return Long.decode(number);
    }
}
