package org.spl.vm.types;

import org.spl.vm.objects.SPLObject;

public class SPLObjectType extends SPLCommonType {

  private SPLObjectType(SPLCommonType type) {
    super(type, "object", SPLObject.class);
  }

  public static SPLObjectType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public final static SPLObjectType instance = new SPLObjectType(null);
  }
}
