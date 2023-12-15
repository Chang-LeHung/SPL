package org.spl.vm.interpreter;

import org.spl.vm.types.SPLCommonType;

public class SPLTraceBackType extends SPLCommonType {
  private SPLTraceBackType() {
    super(null, "traceback", SPLTraceBackObject.class);
  }

  public static SPLTraceBackType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLTraceBackType INSTANCE = new SPLTraceBackType();
  }

}
