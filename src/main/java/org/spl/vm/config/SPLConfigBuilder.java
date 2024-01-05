package org.spl.vm.config;

import java.util.Map;

public class SPLConfigBuilder {

  public static final String CALL_STACK_SIZE_NAME = "SPL_MAX_CALLSTACK_SIZE";
  public static final String CORE_COUNT_NAME = "SPL_CORE_COUNT";
  public static final int DEFAULT_MAX_CALLSTACK_SIZE = 100;

  public static SPLConfiguration build() {
    int maxCallStackSize = DEFAULT_MAX_CALLSTACK_SIZE;
    int coreCount = Runtime.getRuntime().availableProcessors();
    Map<String, String> envs = System.getenv();
    if (envs.containsKey(CALL_STACK_SIZE_NAME)) {
      try {
        maxCallStackSize = Integer.parseInt(envs.get(CALL_STACK_SIZE_NAME));
      } catch (Exception ignore) {
      }
    }
    if (envs.containsKey(CORE_COUNT_NAME)) {
      try {
        coreCount = Integer.parseInt(envs.get(CORE_COUNT_NAME));
      } catch (Exception ignore) {
      }
    }
    return new SPLConfiguration(maxCallStackSize, coreCount);
  }
}
