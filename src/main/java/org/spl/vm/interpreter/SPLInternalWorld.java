package org.spl.vm.interpreter;

import org.spl.vm.config.SPLConfiguration;
import org.spl.vm.splroutine.SPLRoutineObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class SPLInternalWorld {

  public static SPLInternalWorld splWorld;
  public static String mainRoutineName = "main";
  private SPLRoutineObject mainRoutine;
  private final BlockingQueue<SPLRoutineObject> ready;
  private final BlockingQueue<SPLRoutineObject> waiting;
  private final SPLConfiguration config;
  private final ReentrantLock lock;


  public SPLInternalWorld(SPLConfiguration config) {
    this.config = config;
    ready = new LinkedBlockingQueue<>();
    waiting = new LinkedBlockingQueue<>();
    lock = new ReentrantLock();
  }

  public int getMaxCallStackSize() {
    return config.getMaxCallStackSize();
  }

  public int getMaxCoreThreadCount() {
    return config.getMaxCoreThreads();
  }

  private void initializeSPLWorld() {

  }

  private void destroySPLWorld() {

  }

  private void createMainRoutine(DefaultEval eval) {
    mainRoutine = new SPLRoutineObject(eval, mainRoutineName);
    ready.add(mainRoutine);
  }


  private void swapRoutine(SPLRoutineObject routine) {
    routine.setState(SPLRoutineObject.SPLRoutineState.RUNNING);
    ThreadState.get().setCoroutine(routine);
    if (routine.getState() == SPLRoutineObject.SPLRoutineState.INIT) {
      routine.setState(SPLRoutineObject.SPLRoutineState.READY);
    }
    SPLRoutineObject.SPLRoutineState state = routine.resume();
    switch (state) {
      case TERMINATED, ERROR_OCCURRED -> {
        routine.destroy();
      }
      case WAITING -> {
        waiting.add(routine);
      }
    }
  }

  private void controlCenter() {
    while (SPLRoutineObject.nonDaemonRoutineCount.get() != 0) {
      SPLRoutineObject routine = ready.poll();
      if (routine == null) {
        lock.lock();
        try {
          while (!waiting.isEmpty()) {
            SPLRoutineObject o = waiting.poll();
            o.setState(SPLRoutineObject.SPLRoutineState.READY);
            ready.add(o);
          }
        } finally {
          lock.unlock();
        }
        continue;
      }
      swapRoutine(routine);
    }
  }

  public void boot(DefaultEval eval) {
    initializeSPLWorld();
    createMainRoutine(eval);
    controlCenter();
    destroySPLWorld();
  }

  public void addCoroutine(SPLRoutineObject routine, SPLRoutineObject.SPLRoutineState state) {
    switch (state) {
      case READY -> ready.add(routine);
      case WAITING -> waiting.add(routine);
      default -> throw new IllegalStateException("Unexpected value: " + state);
    }
  }


  public void addNewRoutine(SPLRoutineObject routine) {
    assert routine.getState() == SPLRoutineObject.SPLRoutineState.READY;
    ready.add(routine);
  }


  private class SPLWorldWorker implements Runnable {

    @Override
    public void run() {

    }
  }

  public SPLRoutineObject getMainRoutine() {
    return mainRoutine;
  }
}
