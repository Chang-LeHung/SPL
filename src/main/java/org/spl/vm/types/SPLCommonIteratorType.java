package org.spl.vm.types;

import org.spl.vm.objects.SPLCommonIterator;
import org.spl.vm.objects.SPLObject;

public class SPLCommonIteratorType extends SPLCommonType{
  private SPLCommonIteratorType() {
    super(null, "CommonIterator", SPLCommonIterator.class);
  }

  public static class SelfHolder {
    public static final SPLCommonIteratorType INSTANCE = new SPLCommonIteratorType();
  }

  public static SPLCommonIteratorType getInstance() {
    return SelfHolder.INSTANCE;
  }

}
