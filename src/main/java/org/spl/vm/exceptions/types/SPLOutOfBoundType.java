package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLOutOfBoundException;
import org.spl.vm.types.SPLCommonType;

public class SPLOutOfBoundType extends SPLCommonType {
  public SPLOutOfBoundType() {
    super(null, "OutOfBound", SPLOutOfBoundException.class);
  }

  public static SPLOutOfBoundType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLOutOfBoundType INSTANCE = new SPLOutOfBoundType();
  }
}
