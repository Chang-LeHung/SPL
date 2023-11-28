package org.spl.vm.types;

import org.spl.vm.objects.SPLBoolObject;

public class SPLBoolType extends SPLCommonType {

  private SPLBoolType(SPLCommonType type) {
    super(type, "bool", SPLBoolObject.class);
  }

  public static SPLBoolType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPLBoolType instance = new SPLBoolType(null);
  }
}
