package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.interfaces.SPLContinuable;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.types.SPLStaticMethodWrapperType;

public class SPLStaticMethodWrapper extends SPLObject implements SPLContinuable {

  private final SPLFuncObject func;

  public SPLStaticMethodWrapper(SPLFuncObject func) {
    super(SPLStaticMethodWrapperType.getInstance());
    this.func = func;
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    return func.__call__(args);
  }

  @Override
  public SPLObject resume() throws SPLInternalException {
    return func.resume();
  }
}
