package org.spl.vm.types;

import org.spl.vm.objects.SPLModuleObject;
import org.spl.vm.objects.SPLObject;

public class SPLModuleType extends SPLCommonType {
  private SPLModuleType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  public static SPLModuleType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLModuleType INSTANCE = new SPLModuleType(null, "SPLModule", SPLModuleObject.class);
  }
}
