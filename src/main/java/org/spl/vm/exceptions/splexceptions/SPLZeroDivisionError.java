package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLZeroDivisionErrorType;

public class SPLZeroDivisionError extends SPLException{
  public SPLZeroDivisionError(String msg) {
    super(msg, SPLZeroDivisionErrorType.getInstance());
  }
}
