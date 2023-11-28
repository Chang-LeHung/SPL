package org.spl.vm.internal.typs;

import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.objects.SPLFloatObject;
import org.spl.vm.types.SPLCommonType;

public class SPLFrameType extends SPLCommonType {
  private SPLFrameType(SPLCommonType type, String name) {
    super(type, name, SPLFrameObject.class);
  }

  public static SPLFrameType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    static SPLFrameType instance = new SPLFrameType(null, "frame");
  }
}
