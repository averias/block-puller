package com.averiasconnect.blockpuller.scheduler;

import com.averiasconnect.blockpuller.command.SyncBlocksCommand;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleSyncPolygonBlocksTask {
  private final SyncBlocksCommand syncPolygonBlocksCommand;

  @Scheduled(fixedRate = 3, timeUnit = TimeUnit.SECONDS)
  public void sync() {
    syncPolygonBlocksCommand.sync();
  }
}
