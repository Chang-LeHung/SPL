package org.spl.vm.types;

public class SPLLongType extends SPLCommonType {
  private SPLLongType(SPLCommonType type) {
    super(type, "long");
  }

  public static SPLLongType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    static final SPLLongType instance = new SPLLongType(null);
  }
}
