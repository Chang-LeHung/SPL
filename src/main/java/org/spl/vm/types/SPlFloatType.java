package org.spl.vm.types;


import org.spl.vm.objects.SPLFloatObject;

public class SPlFloatType extends SPLCommonType {
  private SPlFloatType(SPLCommonType type) {
    super(type, "float", SPLFloatObject.class);
  }

  public static SPlFloatType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPlFloatType instance = new SPlFloatType(null);
  }
}
