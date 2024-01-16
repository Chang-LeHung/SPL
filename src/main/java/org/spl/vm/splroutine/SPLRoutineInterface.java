package org.spl.vm.splroutine;

public interface SPLRoutineInterface {

  void init();

  SPLRoutineObject.SPLRoutineState resume();

  SPLRoutineObject.SPLRoutineState getState();

  void destroy();
}
