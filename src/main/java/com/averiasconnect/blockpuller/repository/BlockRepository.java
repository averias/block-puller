package com.averiasconnect.blockpuller.repository;

import com.averiasconnect.blockpuller.model.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockRepository extends JpaRepository<BlockEntity, Integer> {
    @Query("select b from BlockEntity b where b.network= :network order by b.number DESC LIMIT 1")
    BlockEntity findLatest(@Param("network") String network);
}
