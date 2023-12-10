package org.spl.vm.internal.typs;

import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class DisType extends SPLCommonType {
  private DisType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  private static class SelfHolder {
    public static final DisType INSTANCE = new DisType(null, "dis", SPLObject.class);
  }

  public static DisType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
