package org.spl.vm.types;

public class SPLBoolType extends SPLCommonType {

  private SPLBoolType(SPLCommonType type) {
    super(type, "bool");
  }

  public static SPLBoolType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPLBoolType instance = new SPLBoolType(null);
  }
}
