package org.spl.vm.types;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.objects.*;

public class SPLLongType extends SPLCommonType {
  private SPLLongType(SPLCommonType type) {
    super(type, "long", SPLLongObject.class);
  }

  public static SPLLongType getInstance() {
    return SelfHolder.instance;
  }

  @Override
  @SPLExportMethod
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    if (args.length == 1) {
      SPLObject o = args[0];
      if (o instanceof SPLStringObject str) {
        return SPLLongObject.create(Integer.parseInt(str.getVal()));
      } else if (o == SPLBoolObject.getTrue()) {
        return SPLLongObject.create(1);
      } else if (o == SPLBoolObject.getFalse()) {
        return SPLLongObject.create(0);
      } else if (o instanceof SPLFloatObject f) {
        return SPLLongObject.create((long) f.getVal());
      }
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError(type.getName() + "'s can not be transformed into long(int)"));
  }

  private static class SelfHolder {
    static final SPLLongType instance = new SPLLongType(null);
  }
}
