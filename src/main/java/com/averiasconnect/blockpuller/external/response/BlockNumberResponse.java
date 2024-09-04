package com.averiasconnect.blockpuller.external.response;

import com.averiasconnect.blockpuller.external.model.BlockNumber;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BlockNumberResponse {
  private final BlockNumber blockNumber;

  @JsonCreator
  public BlockNumberResponse(@JsonProperty("result") String result) {
    this.blockNumber = new BlockNumber(result);
  }
}
