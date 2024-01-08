package org.spl.vm.stlib.vm;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.interfaces.SPLModuleInterface;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.interpreter.SPLInternalWorld;
import org.spl.vm.objects.SPLModuleObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.splroutine.SPLRoutineObject;

public class ModuleMain extends SPLModuleObject implements SPLModuleInterface {
  public ModuleMain() {
    super("vm");
  }

  @Override
  public void init() {

  }

  @Override
  public void destroy() {

  }

  @SPLExportMethod
  public SPLObject spawn(SPLObject... args) throws SPLInternalException {
    if (args.length >= 1) {
      SPLObject arg = args[0];
      if (arg instanceof SPLFuncObject f) {
        f = new SPLFuncObject(f);
        SPLObject[] newArgs = new SPLObject[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        f.buildEval(newArgs);
        int routineCount = SPLRoutineObject.getRoutineCount();
        SPLRoutineObject.increaseRoutineCount();
        SPLRoutineObject routine = new SPLRoutineObject(f, "SPLRoutine-" + routineCount);
        routine.setState(SPLRoutineObject.SPLRoutineState.READY);
        SPLInternalWorld.splWorld.addReadyRoutine(routine);
        return routine;
      }
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Invalid arguments to spawn a new routine"));
  }
}
