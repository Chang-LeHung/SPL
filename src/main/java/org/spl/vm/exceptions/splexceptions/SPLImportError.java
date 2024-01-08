package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLAttributeErrorType;

public class SPLImportError extends SPLException {
  public SPLImportError(String msg) {
    super(msg, SPLAttributeErrorType.getInstance());
  }
}
