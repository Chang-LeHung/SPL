package org.spl.vm.exceptions.types;

import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLAttributeError extends SPLCommonType {
  public SPLAttributeError() {
    super(null, "AttributeError", SPLAttributeError.class);
  }

  private static class SelfHolder {
    static SPLAttributeError instance = new SPLAttributeError();
  }

  public static SPLAttributeError getInstance() {
    return SelfHolder.instance;
  }
}
