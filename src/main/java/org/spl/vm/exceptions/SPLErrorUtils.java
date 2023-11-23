package org.spl.vm.exceptions;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.interpreter.ThreadState;
import org.spl.vm.objects.SPLObject;

public class SPLErrorUtils {

  public static SPLObject splErrorFormat(SPLException error) throws SPLInternalException {
    ThreadState ts = ThreadState.get();
    ts.setExecType(error.getType());
    ts.setExecVal(error);
    throw new SPLInternalException(error.getMsg());
  }
}
