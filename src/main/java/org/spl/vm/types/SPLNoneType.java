package org.spl.vm.types;

import org.spl.vm.objects.SPLNoneObject;

public class SPLNoneType extends SPLCommonType {
  SPLNoneType(SPLCommonType type, String name) {
    super(type, name, SPLNoneObject.class);
  }

  private static class SelfHolder {
    public static final SPLNoneType instance = new SPLNoneType(null, "none");
  }

  public static SPLNoneType getInstance() {
    return SelfHolder.instance;
  }

}
