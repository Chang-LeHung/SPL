package org.spl.vm.exceptions.splexceptions;

public class SPLTypeError extends SPLException {

  public SPLTypeError(String msg) {
    super(msg, org.spl.vm.exceptions.types.SPLTypeError.getInstance());
  }
}
