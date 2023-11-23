package org.spl.vm.types;

public class SPLCallType extends SPLCommonType{
  public SPLCallType(SPLCommonType type, String name) {
    super(type, name);
  }

  private static class SelfHolder {
    public static final SPLCallType INSTANCE = new SPLCallType(null, "call");
  }

  public static SPLCallType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
