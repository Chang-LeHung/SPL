package org.spl.vm.types;

import org.spl.vm.objects.SPLStringObject;

public class SPLStringType extends SPLCommonType {
  public SPLStringType(SPLCommonType type) {
    super(type, "str", SPLStringObject.class);
  }

  public static SPLStringType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public static final SPLStringType instance = new SPLStringType(null);
  }
}
