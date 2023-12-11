package org.spl.vm.exceptions.types;

import org.spl.vm.builtin.Builtin;
import org.spl.vm.types.SPLCommonType;

public class SPLTypeError extends SPLCommonType {
  public SPLTypeError(SPLCommonType type) {
    super(type, "TypeError", SPLTypeError.class);
  }

  public static SPLTypeError getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    final static SPLTypeError instance = new SPLTypeError(null);

    static {
      Builtin.addObject("TypeError", instance);
    }
  }
}
