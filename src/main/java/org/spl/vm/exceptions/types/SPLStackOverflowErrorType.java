package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLStackOverflowError;
import org.spl.vm.types.SPLCommonType;

public class SPLStackOverflowErrorType extends SPLCommonType {
  public SPLStackOverflowErrorType() {
    super(null, "StackOverflowError", SPLStackOverflowError.class);
  }

  public static SPLStackOverflowErrorType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    static final SPLStackOverflowErrorType INSTANCE = new SPLStackOverflowErrorType();
  }
}
