package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLRuntimeExceptionType extends SPLCommonType {
  private SPLRuntimeExceptionType() {
    super(null, "RuntimeException", SPLRuntimeException.class);
  }

  private static class SelfHolder {
    public static final SPLRuntimeExceptionType INSTANCE = new SPLRuntimeExceptionType();
  }

  public static SPLRuntimeExceptionType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
