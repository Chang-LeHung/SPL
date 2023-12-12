package org.spl.vm.exceptions.types;

import org.spl.vm.builtin.Builtin;
import org.spl.vm.types.SPLCommonType;

public class SPLAttributeErrorType extends SPLCommonType {
  public SPLAttributeErrorType() {
    super(null, "AttributeError", SPLAttributeErrorType.class);
  }

  public static SPLAttributeErrorType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    static SPLAttributeErrorType instance = new SPLAttributeErrorType();

    static {
      Builtin.addObject("AttributeError", getInstance());
    }
  }
}
