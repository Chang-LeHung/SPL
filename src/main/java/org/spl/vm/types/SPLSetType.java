package org.spl.vm.types;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.objects.SPLCommonIterator;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLSetObject;
import org.spl.vm.objects.SPLStopIteration;

import java.util.HashSet;

public class SPLSetType extends SPLCommonType {
  private SPLSetType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  public static SPLSetType getInstance() {
    return SelfHolder.instance;
  }

  private static class SelfHolder {
    public static final SPLSetType instance = new SPLSetType(null, "Set", SPLSetObject.class);
  }

  @Override
  @SPLExportMethod
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    if (args.length != 1) {
      throw new SPLInternalException("list_index requires exactly one argument");
    }
    SPLObject o = args[0];
    try {
      SPLCommonIterator iterator = o.__getIterator__();
      SPLObject n;
      HashSet<SPLObject> res = new HashSet<>();
      do {
        n = iterator.next();
        if (n != SPLStopIteration.getInstance()) {
          res.add(n);
        }
      } while (n != SPLStopIteration.getInstance());
      return new SPLSetObject(res);
    } catch (Exception ignore){}
    return SPLErrorUtils.splErrorFormat(new SPLTypeError(o.__str__() + " can not be transformed into  a list"));
  }
}
