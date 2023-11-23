package org.spl.vm.types;

public class SPLObjectType extends SPLCommonType {

  private SPLObjectType(SPLCommonType type) {
    super(type, "object");
  }

  public static SPLObjectType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPLObjectType instance = new SPLObjectType(null);
  }
}
