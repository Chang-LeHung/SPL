package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.splexceptions.SPLClassBuildError;
import org.spl.vm.types.SPLCommonType;

public class SPLClassBuildErrorType extends SPLCommonType {
  private SPLClassBuildErrorType() {
    super(null, "ClassBuildError", SPLClassBuildError.class);
  }

  public static SPLClassBuildErrorType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    private static final SPLClassBuildErrorType INSTANCE = new SPLClassBuildErrorType();
  }
}
