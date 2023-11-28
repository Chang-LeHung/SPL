package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.types.SPLCommonType;
import org.spl.vm.types.SPLObjectType;

public class SPLExceptionType extends SPLCommonType {


  private SPLExceptionType(SPLObjectType type) {
    super(type, "Exception", SPLException.class);
  }

  public static SPLExceptionType getInstance(SPLObjectType type) {
    return new SPLExceptionType(type);
  }

  public static SPLExceptionType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    private final static SPLExceptionType instance = new SPLExceptionType(null);
  }
}
