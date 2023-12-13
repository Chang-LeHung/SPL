package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLStackOverflowError;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLStackOverflowErrorType extends SPLCommonType {
  public SPLStackOverflowErrorType() {
    super(null, "StackOverflowError", SPLStackOverflowError.class);
  }

  private static class SelfHolder {
    static final SPLStackOverflowErrorType INSTANCE = new SPLStackOverflowErrorType();
  }

  public static SPLStackOverflowErrorType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
