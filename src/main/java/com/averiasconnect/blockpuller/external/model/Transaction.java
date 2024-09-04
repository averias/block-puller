package com.averiasconnect.blockpuller.external.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    public String blockHash;
    public String blockNumber;
    public String from;
    public String gas;
    public String gasPrice;
    public String maxFeePerGas;
    public String maxPriorityFeePerGas;
    public String hash;
    public String input;
    public String nonce;
    public String to;
    public String transactionIndex;
    public String value;
    public String type;
    public String chainId;
    public String v;
    public String r;
    public String s;
    public String yParity;
    public String timestamp;

    public Transaction setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "blockHash='" + blockHash + '\'' +
                ", blockNumber=" + blockNumber +
                ", from='" + from + '\'' +
                ", gas='" + gas + '\'' +
                ", gasPrice='" + gasPrice + '\'' +
                ", maxFeePerGas='" + maxFeePerGas + '\'' +
                ", maxPriorityFeePerGas='" + maxPriorityFeePerGas + '\'' +
                ", hash='" + hash + '\'' +
                ", input='" + input + '\'' +
                ", nonce='" + nonce + '\'' +
                ", to='" + to + '\'' +
                ", transactionIndex='" + transactionIndex + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", chainId='" + chainId + '\'' +
                ", v='" + v + '\'' +
                ", r='" + r + '\'' +
                ", s='" + s + '\'' +
                ", yParity='" + yParity + '\'' +
                '}';
    }
}
