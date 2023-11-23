package org.spl.vm.types;

public class SPLStringType extends SPLCommonType {
  public SPLStringType(SPLCommonType type) {
    super(type, "str");
  }

  public static SPLStringType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public static final SPLStringType instance = new SPLStringType(null);
  }
}
