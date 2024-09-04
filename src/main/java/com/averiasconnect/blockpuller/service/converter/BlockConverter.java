package com.averiasconnect.blockpuller.service.converter;

import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.model.BlockEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

@Component
public class BlockConverter {
  public BlockEntity convertToEntity(Block block) {
    BlockEntity blockEntity = new BlockEntity();
    return blockEntity
        .setId(block.getBlockNumberAsLong())
        .setHash(block.hash)
        .setNumber(block.number)
        .setParentHash(block.parentHash)
        .setNetwork(block.network)
        .setTimestamp(
            Instant.ofEpochMilli(Integer.decode(block.timestamp) * 1000L)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime());
  }
}
