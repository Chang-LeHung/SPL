package org.spl.vm.types;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

public class SPLStringType extends SPLCommonType {
  public SPLStringType(SPLCommonType type) {
    super(type, "str", SPLStringObject.class);
  }

  public static SPLStringType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public static final SPLStringType instance = new SPLStringType(null);
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    if (args.length == 1) {
      return args[0].__str__();
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Invalid number of arguments for str function"));
  }
}
