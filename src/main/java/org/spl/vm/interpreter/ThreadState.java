package org.spl.vm.interpreter;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.splroutine.SPLRoutineObject;
import org.spl.vm.types.SPLCommonType;

public class ThreadState {

  private volatile SPLRoutineObject coroutine;

  public static ThreadLocal<ThreadState> tss;

  static {
    tss = new ThreadLocal<>();
  }

  public static ThreadState get() {
    ThreadState ts = tss.get();
    if (null == ts) {
      ts = new ThreadState();
      tss.set(ts);
    }
    return ts;
  }

  public static void set(ThreadState ts) {
    tss.set(ts);
  }

  public SPLRoutineObject swapCurrentCoroutine(SPLRoutineObject coroutine) {
    SPLRoutineObject old = this.coroutine;
    this.coroutine = coroutine;
    return old;
  }

  public SPLRoutineObject getCurrentRoutine() {
    return coroutine;
  }

  public SPLRoutineObject getCoroutineOfCurrentThread() {
    return get().getCurrentRoutine();
  }

  public static void clearCurrentCoroutineState() {
    get().getCurrentRoutine().setExecType(null);
    get().getCurrentRoutine().setExecVal(null);
    get().getCurrentRoutine().setTrace(null);
  }

  public static void increaseThreadCallStackSize() throws SPLInternalException {
    get().getCurrentRoutine().increaseCallStackSize();
  }

  public static void decreaseThreadCallStackSize() throws SPLInternalException {
    get().getCurrentRoutine().decreaseCallStackSize();
  }


  public int getCallStackSize() {
    return coroutine.getCallStackSize();
  }

  public SPLCommonType getExecType() {
    return coroutine.getExecType();
  }

  public SPLException getExecVal() {
    return coroutine.getExecVal();
  }

  public SPLTraceBackObject getTrace() {
    return coroutine.getTrace();
  }

  public SPLFrameObject getCurrentFrame() {
    return coroutine.getCurrentFrame();
  }

  public void setExecType(SPLCommonType execType) {
    coroutine.setExecType(execType);
  }

  public void setExecVal(SPLException execVal) {
    coroutine.setExecVal(execVal);
  }

  public void setTrace(SPLTraceBackObject trace) {
    coroutine.setTrace(trace);
  }

  public void setCurrentFrame(SPLFrameObject currentFrame) {
    coroutine.setCurrentFrame(currentFrame);
  }

  public void setCoroutine(SPLRoutineObject coroutine) {
    this.coroutine = coroutine;
  }
}
