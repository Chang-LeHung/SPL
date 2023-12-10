package org.spl.vm.types;

import org.spl.vm.objects.SPLCallObject;

public class SPLCallType extends SPLCommonType {
  public SPLCallType(SPLCommonType type, String name) {
    super(type, name, SPLCallObject.class);
  }

  public static SPLCallType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLCallType INSTANCE = new SPLCallType(null, "call");
  }
}
