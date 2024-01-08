package org.spl.vm.stlib.time;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.interfaces.SPLModuleInterface;
import org.spl.vm.interpreter.SPLInternalWorld;
import org.spl.vm.interpreter.ThreadState;
import org.spl.vm.objects.SPLLongObject;
import org.spl.vm.objects.SPLModuleObject;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.splroutine.SPLRoutineMarker;
import org.spl.vm.splroutine.SPLRoutineObject;

public class ModuleMain extends SPLModuleObject implements SPLModuleInterface {
  public ModuleMain() {
    super("time");
  }

  @SPLExportMethod
  public SPLObject sleep(SPLObject... args) throws SPLInternalException {
    if (args.length == 1) {
      SPLObject arg = args[0];
      if (arg instanceof SPLLongObject l) {
        SPLRoutineObject routine = ThreadState.get().getCurrentRoutine();
        SPLInternalWorld.splWorld.addTimeWaitingRoutine(routine, l.getVal() + System.currentTimeMillis());
        SPLRoutineMarker.TIME_WAITING.setNeedReCall(false);
        return SPLRoutineMarker.TIME_WAITING;
      }
      return SPLNoneObject.getInstance();
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("sleep requires a long argument"));
  }

  @Override
  public void init() {

  }

  @Override
  public void destroy() {

  }
}
