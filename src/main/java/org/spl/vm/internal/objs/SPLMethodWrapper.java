package org.spl.vm.internal.objs;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.interfaces.SPLContinuable;
import org.spl.vm.internal.typs.SPLMethodWrapperType;
import org.spl.vm.objects.SPLObject;

public class SPLMethodWrapper extends SPLObject implements SPLContinuable {

  private final SPLFuncObject func;
  private SPLObject self;

  public SPLMethodWrapper(SPLFuncObject func, SPLObject self) {
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

  public void buildEval(SPLObject... args) throws SPLInternalException {
    SPLObject[] newArgs = new SPLObject[args.length + 1];
    newArgs[0] = self;
    System.arraycopy(args, 0, newArgs, 1, args.length);
    func.buildEval(newArgs);
  }

  @Override
  public SPLObject resume() throws SPLInternalException {
    return func.resume();
  }
}
