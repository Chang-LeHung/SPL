package org.spl.vm.types;

public class SPLNoneType extends SPLCommonType {
  SPLNoneType(SPLCommonType type, String name) {
    super(type, name);
  }

  private static class SelfHolder {
    public static final SPLNoneType instance = new SPLNoneType(null, "None");
  }

  public static SPLNoneType getInstance() {
    return SelfHolder.instance;
  }

}
