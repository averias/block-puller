package com.averiasconnect.blockpuller.service.converter;

import com.averiasconnect.blockpuller.external.model.Transaction;

import com.averiasconnect.blockpuller.model.avro.TransactionRecord;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter {
  public TransactionRecord toAvroTransactionRecord(Transaction tx) {
    return TransactionRecord.newBuilder()
            .setBlockHash(tx.blockHash)
            .setBlockNumber(tx.blockNumber)
            .setFrom(tx.from)
            .setGas(tx.gas)
            .setGasPrice(tx.gasPrice)
            .setMaxFeePerGas(tx.maxFeePerGas)
            .setMaxPriorityFeePerGas(tx.maxPriorityFeePerGas)
            .setHash(tx.hash)
            .setInput(tx.input)
            .setNonce(tx.nonce)
            .setTo(tx.to)
            .setTransactionIndex(tx.transactionIndex)
            .setValue(tx.value)
            .setType(tx.type)
            .setChainId(tx.chainId)
            .setV(tx.v)
            .setR(tx.r)
            .setS(tx.s)
            .setYParity(tx.yParity)
            .setTimestamp(tx.timestamp)
            .build();
  }
}
