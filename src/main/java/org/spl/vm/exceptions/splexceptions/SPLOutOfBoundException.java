package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLOutOfBoundType;

public class SPLOutOfBoundException extends SPLException {
  public SPLOutOfBoundException(String msg) {
    super(msg, SPLOutOfBoundType.getInstance());
  }
}
