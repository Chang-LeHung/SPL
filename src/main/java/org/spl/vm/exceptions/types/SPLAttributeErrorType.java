package org.spl.vm.exceptions.types;

import org.spl.vm.types.SPLCommonType;

public class SPLAttributeErrorType extends SPLCommonType {
  public SPLAttributeErrorType() {
    super(null, "AttributeError", SPLAttributeErrorType.class);
  }

  private static class SelfHolder {
    static SPLAttributeErrorType instance = new SPLAttributeErrorType();
  }

  public static SPLAttributeErrorType getInstance() {
    return SelfHolder.instance;
  }
}
