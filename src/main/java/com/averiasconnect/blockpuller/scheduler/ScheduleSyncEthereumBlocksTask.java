package com.averiasconnect.blockpuller.scheduler;

import com.averiasconnect.blockpuller.command.SyncBlocksCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ScheduleSyncEthereumBlocksTask {
  private final SyncBlocksCommand syncEthereumBlocksCommand;

  @Scheduled(fixedRate = 12, timeUnit = TimeUnit.SECONDS)
  public void sync() {
    syncEthereumBlocksCommand.sync();
  }
}
