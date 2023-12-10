package org.spl.vm.types;


import org.spl.vm.objects.SPLListObject;

public class SPLListType extends SPLCommonType {
  private SPLListType() {
    super(null, "list", SPLListObject.class);
  }

  public static SPLListType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public static SPLListType instance = new SPLListType();
  }
}
