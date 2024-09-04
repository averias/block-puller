package com.averiasconnect.blockpuller.external.response;

import com.averiasconnect.blockpuller.external.model.Block;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BlockResponse {
    private final Block block;

    @JsonCreator
    public BlockResponse(@JsonProperty("result") Block block) {
        this.block = block;
    }

    public Block getBlock(String network) {
        block.network = network;
        return block;
    }
}
