package org.spl.vm.types;


import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.objects.SPLCommonIterator;
import org.spl.vm.objects.SPLListObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStopIteration;

import java.util.ArrayList;

public class SPLListType extends SPLCommonType {
  private SPLListType() {
    super(null, "list", SPLListObject.class);
  }

  public static SPLListType getInstance() {
    return SelfHolder.instance;
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
      ArrayList<SPLObject> res = new ArrayList<>();
      do {
        n = iterator.next();
        if (n != SPLStopIteration.getInstance()) {
          res.add(n);
        }
      } while (n != SPLStopIteration.getInstance());
      return new SPLListObject(res);
    } catch (Exception ignore) {
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError(o.__str__() + " can not be transformed into  a list"));
  }

  private static class SelfHolder {
    public static SPLListType instance = new SPLListType();
  }
}
