package org.spl.vm.config;

public class SPLConfiguration {
  private final int maxCallStackSize;

  private final int maxCoreThreads;

  public SPLConfiguration(int maxCallStackSize, int maxCoreThreads) {
    this.maxCallStackSize = maxCallStackSize;
    this.maxCoreThreads = maxCoreThreads;
  }

  public int getMaxCallStackSize() {
    return maxCallStackSize;
  }

  public int getMaxCoreThreads() {
    return maxCoreThreads;
  }
}
