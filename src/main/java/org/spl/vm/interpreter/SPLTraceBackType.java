package org.spl.vm.interpreter;

import org.spl.compiler.ir.binaryop.GreaterThan;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLTraceBackType extends SPLCommonType {
  private SPLTraceBackType() {
    super(null, "traceback", SPLTraceBackObject.class);
  }

  private static class SelfHolder {
    public static final SPLTraceBackType INSTANCE = new SPLTraceBackType();
  }

  public static SPLTraceBackType getInstance() {
    return SelfHolder.INSTANCE;
  }

}
