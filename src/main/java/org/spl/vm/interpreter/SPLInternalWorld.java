package org.spl.vm.interpreter;

import org.spl.vm.config.SPLConfiguration;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.impsys.SPLLoader;
import org.spl.vm.objects.SPLModuleObject;
import org.spl.vm.splroutine.SPLRoutineObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class SPLInternalWorld {

  public static SPLInternalWorld splWorld;
  public static String mainRoutineName = "main";
  private SPLRoutineObject mainRoutine;
  private final BlockingQueue<SPLRoutineObject> ready;
  private final BlockingQueue<SPLRoutineObject> waiting;
  private final ConcurrentHashMap<SPLRoutineObject, Long> timeWaiting;
  private final SPLConfiguration config;
  private final ReentrantLock lock;
  private final SPLLoader loader;
  private final Map<String, SPLModuleObject> modules;
  private volatile boolean inChecking;
  private final Set<SPLWorldWorker> workers;


  public SPLInternalWorld(SPLConfiguration config) {
    this.config = config;
    ready = new LinkedBlockingQueue<>();
    waiting = new LinkedBlockingQueue<>();
    lock = new ReentrantLock();
    timeWaiting = new ConcurrentHashMap<>();
    inChecking = false;
    loader = new SPLLoader();
    modules = new ConcurrentHashMap<>();
    workers = new CopyOnWriteArraySet<>();
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
      case READY -> {
        ready.add(routine);
      }
    }
  }

  private void checkTimeWaitingRoutines() {
    if (inChecking) return;
    inChecking = true;
    Set<Map.Entry<SPLRoutineObject, Long>> entries = timeWaiting.entrySet();
    for (Map.Entry<SPLRoutineObject, Long> entry : entries) {
      if (entry.getValue() <=  System.currentTimeMillis()) {
        if (entries.remove(entry)) {
          SPLRoutineObject routine = entry.getKey();
          routine.setState(SPLRoutineObject.SPLRoutineState.READY);
          ready.add(routine);
        }
      }
    }
    inChecking = false;
  }


  private void adaptiveAddThread() {
    boolean s = lock.tryLock();
    if (!s) return;
    try {
      if (ready.size() > 1 && workers.size() < config.getMaxCoreThreads()) {
        SPLWorldWorker worker = new SPLWorldWorker();
        worker.start();
        workers.add(worker);
      }
    } finally {
      lock.unlock();
    }
  }

  private void controlCenter() {
    while (SPLRoutineObject.nonDaemonRoutineCount.get() != 0) {
      checkTimeWaitingRoutines();
      SPLRoutineObject routine = ready.poll();
      adaptiveAddThread();
      if (routine == null) {
        moveWaitingRoutineToReady();
        continue;
      }
      swapRoutine(routine);
    }
  }

  private void moveWaitingRoutineToReady() {
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

  public void addReadyRoutine(SPLRoutineObject routine) {
    assert routine.getState() == SPLRoutineObject.SPLRoutineState.READY;
    ready.add(routine);
  }

  public SPLModuleObject loadModule(String moduleName) throws SPLInternalException {
    if (modules.containsKey(moduleName))
      return modules.get(moduleName);
    SPLModuleObject m = loader.load(moduleName);
    modules.put(moduleName, m);
    return m;
  }

  public void addTimeWaitingRoutine(SPLRoutineObject routine,  long time) {
    timeWaiting.put(routine, time);
  }

  private class SPLWorldWorker extends Thread {

    @Override
    public void run() {
      controlCenter();
    }
  }

  public SPLRoutineObject getMainRoutine() {
    return mainRoutine;
  }
}
