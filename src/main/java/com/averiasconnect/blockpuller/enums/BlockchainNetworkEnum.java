package com.averiasconnect.blockpuller.enums;

import lombok.Getter;

@Getter
public enum BlockchainNetworkEnum {
    POLYGON("polygon"),
    OPTIMISM("optimism"),
    ETHEREUM("ethereum");

    private final String value;

    BlockchainNetworkEnum(String value) {
        this.value = value;
    }
}
