package org.spl.vm.internal.typs;

import org.spl.vm.internal.utils.SPLRangeObject;
import org.spl.vm.types.SPLCommonType;

public class SPLRangeType extends SPLCommonType {
  public SPLRangeType() {
    super(null, "range", SPLRangeObject.class);
  }

  public static SPLRangeType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLRangeType INSTANCE = new SPLRangeType();
  }
}
