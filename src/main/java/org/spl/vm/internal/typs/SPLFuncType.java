package org.spl.vm.internal.typs;

import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.types.SPLCommonType;

public class SPLFuncType extends SPLCommonType {
  private SPLFuncType(SPLCommonType type, String name, Class<?> clazz) {
    super(type, name, clazz);
  }

  private static class SelfHolder {
    public static final SPLFuncType INSTANCE = new SPLFuncType(null, "func", SPLFuncObject.class);
  }

  public static SPLFuncType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
