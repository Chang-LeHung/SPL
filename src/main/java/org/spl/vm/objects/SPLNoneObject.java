package org.spl.vm.objects;

import org.spl.vm.types.SPLCommonType;
import org.spl.vm.types.SPLNoneType;

public class SPLNoneObject extends SPLObject{
  private SPLNoneObject(SPLCommonType type) {
    super(type);
  }

  private static class  SingletonHolder {
    private static final SPLNoneObject INSTANCE = new SPLNoneObject(SPLNoneType.getInstance());
  }

  public static SPLNoneObject getInstance() {
    return SingletonHolder.INSTANCE;
  }
}
