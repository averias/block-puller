package com.averiasconnect.blockpuller.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "blocks")
public class BlockEntity {
    @Id
    private Long id;
    @Column(length=100, nullable=false)
    private String hash;
    @Column(length=100, nullable=false)
    private String number;
    @Column(name="parent_hash", length=100, nullable=false)
    private String parentHash;
    @Column(nullable=false)
    private LocalDateTime timestamp;
    @Column(length=20, nullable=false)
    private String network;


    public BlockEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public BlockEntity setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public BlockEntity setNumber(String number) {
        this.number = number;
        return this;
    }

    public BlockEntity setParentHash(String parentHash) {
        this.parentHash = parentHash;
        return this;
    }

    public BlockEntity setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public BlockEntity setNetwork(String network) {
        this.network = network;
        return this;
    }
}
