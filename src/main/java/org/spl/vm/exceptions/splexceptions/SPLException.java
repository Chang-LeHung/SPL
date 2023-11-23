package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.types.SPLExceptionType;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLException extends SPLObject {

  protected String msg;

  public SPLException(String msg) {
    super(SPLExceptionType.getInstance());
    this.msg = msg;
  }

  public SPLException(String msg, SPLCommonType type) {
    super(type);
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
