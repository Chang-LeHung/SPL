package org.spl.vm.exceptions.types;

import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLImportErrorType extends SPLCommonType {
  private SPLImportErrorType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  public static SPLImportErrorType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    static final SPLImportErrorType INSTANCE = new SPLImportErrorType(null, "ImportError", null);
  }
}
