package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLRuntimeExceptionType;

public class SPLRuntimeException extends SPLException {
  public SPLRuntimeException(String msg) {
    super(msg, SPLRuntimeExceptionType.getInstance());
  }
}
