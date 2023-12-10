package org.spl.vm.internal.typs;

import org.spl.vm.internal.objs.SPLMethodWrapper;
import org.spl.vm.types.SPLCommonType;

public class SPLMethodWrapperType extends SPLCommonType {
  private SPLMethodWrapperType() {
    super(null, "method_wrapper", SPLMethodWrapper.class);

  }

  public static SPLMethodWrapperType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public static SPLMethodWrapperType instance = new SPLMethodWrapperType();
  }

}
