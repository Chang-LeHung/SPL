package org.spl.vm.types;

import org.spl.vm.objects.SPLDictObject;
import org.spl.vm.objects.SPLObject;

public class SPLDictType extends SPLCommonType {
  private SPLDictType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }


  private static class SelfHolder {
    public static final SPLDictType INSTANCE = new SPLDictType(null, "dict", SPLDictObject.class);
  }

  public static SPLDictType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
