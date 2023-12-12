package org.spl.vm.types;

import org.spl.vm.objects.SPLStopIteration;

public class SPLStopIterationType extends SPLCommonType {
  private SPLStopIterationType() {
    super(null, "StopIteration", SPLStopIteration.class);
  }

  public static SPLStopIterationType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLStopIterationType INSTANCE = new SPLStopIterationType();
  }
}
