package org.spl.vm.exceptions.types;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLLevelObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

import java.util.Map;

public class SPLLevelType extends SPLCommonType {
  public SPLLevelType(SPLCommonType type,
                      String name,
                      Class<? extends SPLObject> clazz,
                      SPLCommonType base,
                      Map<SPLObject, SPLObject> attrs) {
    super(type, name, clazz);
    this.base = base;
    this.attrs = attrs;
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    SPLObject splObject = attrs.get(__init__);
    SPLLevelObject res = new SPLLevelObject(this);
    SPLObject[] newArgs = new SPLObject[args.length + 1];
    newArgs[0] = res;
    System.arraycopy(args, 0, newArgs, 1, args.length);
    splObject.__call__(newArgs);
    return res;
  }

  @Override
  public SPLObject __getMethod__(SPLObject name) throws SPLInternalException {
    if (attrs != null && attrs.containsKey(name)) {
      return attrs.get(name);
    }
    return super.__getMethod__(name);
  }
}
