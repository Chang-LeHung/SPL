package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLClassBuildErrorType;

public class SPLClassBuildError extends SPLException{
  public SPLClassBuildError(String msg) {
    super(msg, SPLClassBuildErrorType.getInstance());
  }
}
