package org.spl.vm.types;

import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLSetObject;

public class SPLSetType extends SPLCommonType{
  private SPLSetType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  private static class SelfHolder {
    public static final SPLSetType instance = new SPLSetType(null, "Set", SPLSetObject.class);
  }

  public static SPLSetType getInstance() {
    return SelfHolder.instance;
  }
}
