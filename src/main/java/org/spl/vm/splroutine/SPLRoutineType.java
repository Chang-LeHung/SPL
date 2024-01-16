package org.spl.vm.splroutine;

import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLRoutineType extends SPLCommonType {
  private SPLRoutineType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  public static SPLRoutineType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLRoutineType INSTANCE = new SPLRoutineType(null, "Routine", SPLRoutineObject.class);
  }
}
