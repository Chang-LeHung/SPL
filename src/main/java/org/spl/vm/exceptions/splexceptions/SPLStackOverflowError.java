package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLStackOverflowErrorType;

public class SPLStackOverflowError extends SPLException {
  public SPLStackOverflowError(String msg) {
    super(msg, SPLStackOverflowErrorType.getInstance());
  }
}
