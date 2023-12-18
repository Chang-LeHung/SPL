package org.spl.vm.types;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.objects.SPLDictObject;
import org.spl.vm.objects.SPLObject;

public class SPLDictType extends SPLCommonType {
  private SPLDictType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  public static SPLDictType getInstance() {
    return SelfHolder.INSTANCE;
  }

  private static class SelfHolder {
    public static final SPLDictType INSTANCE = new SPLDictType(null, "dict", SPLDictObject.class);
  }

  @Override
  @SPLExportMethod
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    if (args.length == 0) {
      return new SPLDictObject();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot call " + getType() + " with " + args.length + " arguments"));
  }
}
