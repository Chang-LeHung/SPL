package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.types.SPLCommonType;
import org.spl.vm.types.SPLNoneType;

public class SPLNoneObject extends SPLObject {
  public static SPLStringObject name = new SPLStringObject("None");

  private SPLNoneObject(SPLCommonType type) {
    super(type);
  }

  public static SPLNoneObject getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    return this == rhs ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    return this == rhs ? SPLBoolObject.getFalse() : SPLBoolObject.getTrue();
  }

  @Override
  public SPLObject __str__() {
    return name;
  }

  private static class SingletonHolder {
    private static final SPLNoneObject INSTANCE = new SPLNoneObject(SPLNoneType.getInstance());
  }
}
