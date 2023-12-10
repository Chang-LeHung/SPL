package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLAttributeErrorType;

public class SPLAttributeError extends SPLException {
  public SPLAttributeError(String msg) {
    super(msg, SPLAttributeErrorType.getInstance());
  }
}
