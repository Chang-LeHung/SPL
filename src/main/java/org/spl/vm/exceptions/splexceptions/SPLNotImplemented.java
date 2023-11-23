package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLNotImplementedType;

public class SPLNotImplemented extends SPLException {
  public SPLNotImplemented(String msg) {
    super(msg, SPLNotImplementedType.getInstance());
  }
}
