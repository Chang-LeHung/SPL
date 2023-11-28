package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLNotImplemented;
import org.spl.vm.types.SPLCommonType;

public class SPLNotImplementedType extends SPLCommonType {
  private SPLNotImplementedType(SPLCommonType type, String name) {
    super(type, name, SPLNotImplemented.class);
  }

  public static SPLNotImplementedType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPLNotImplementedType instance = new SPLNotImplementedType(null, "NotImplemented");
  }
}
