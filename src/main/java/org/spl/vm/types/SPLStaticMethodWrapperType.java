package org.spl.vm.types;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStaticMethodWrapper;

public class SPLStaticMethodWrapperType extends SPLCommonType {
  private SPLStaticMethodWrapperType() {
    super(null, "SPLStaticMethodWrapper", SPLStaticMethodWrapper.class);
  }

  public static SPLStaticMethodWrapperType getInstance() {
    return SelfHolder.INSTANCE;
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    if (args.length == 1) {
      SPLObject arg = args[0];
      if (arg instanceof SPLFuncObject f) {
        return new SPLStaticMethodWrapper(f);
      }
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("static method wrapper takes a function as argument"));
  }

  private static class SelfHolder {
    public static final SPLStaticMethodWrapperType INSTANCE = new SPLStaticMethodWrapperType();
  }
}
