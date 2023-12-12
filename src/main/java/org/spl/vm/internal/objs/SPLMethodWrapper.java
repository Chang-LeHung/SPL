package org.spl.vm.internal.objs;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.typs.SPLMethodWrapperType;
import org.spl.vm.objects.SPLObject;

public class SPLMethodWrapper extends SPLObject {

  private final SPLObject func;
  private SPLObject self;

  public SPLMethodWrapper(SPLObject func, SPLObject self) {
    super(SPLMethodWrapperType.getInstance());
    this.func = func;
    this.self = self;
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    SPLObject[] newArgs = new SPLObject[args.length + 1];
    newArgs[0] = self;
    System.arraycopy(args, 0, newArgs, 1, args.length);
    return func.__call__(newArgs);
  }

  public SPLObject getSelf() {
    return self;
  }

  public void setSelf(SPLObject self) {
    this.self = self;
  }
}
