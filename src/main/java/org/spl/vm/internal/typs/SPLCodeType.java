package org.spl.vm.internal.typs;

import org.spl.vm.types.SPLCommonType;

public class SPLCodeType extends SPLCommonType {
  private SPLCodeType(SPLCommonType type) {
    super(type, "code");
  }

  public static SPLCodeType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    private static final SPLCodeType instance = new SPLCodeType(null);
  }
}
