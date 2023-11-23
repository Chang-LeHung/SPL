package org.spl.vm.types;


public class SPlFloatType extends SPLCommonType {
  private SPlFloatType(SPLCommonType type) {
    super(type, "float");
  }

  public static SPlFloatType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPlFloatType instance = new SPlFloatType(null);
  }
}
