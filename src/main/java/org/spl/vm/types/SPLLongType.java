package org.spl.vm.types;

import org.spl.vm.objects.SPLLongObject;

public class SPLLongType extends SPLCommonType {
  private SPLLongType(SPLCommonType type) {
    super(type, "long", SPLLongObject.class);
  }

  public static SPLLongType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    static final SPLLongType instance = new SPLLongType(null);
  }
}
