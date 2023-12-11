package org.spl.vm.exceptions.types;

import org.spl.vm.builtin.Builtin;
import org.spl.vm.exceptions.splexceptions.SPLZeroDivisionError;
import org.spl.vm.types.SPLCommonType;

public class SPLZeroDivisionErrorType extends SPLCommonType {
  private SPLZeroDivisionErrorType() {
    super(null, "ZeroDivisionError", SPLZeroDivisionError.class);
  }

  private static class SelfHolder {
    public static final SPLZeroDivisionErrorType INSTANCE = new SPLZeroDivisionErrorType();

    static {
      Builtin.addObject("ZeroDivisionError", INSTANCE);
    }
  }

  public static SPLZeroDivisionErrorType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
