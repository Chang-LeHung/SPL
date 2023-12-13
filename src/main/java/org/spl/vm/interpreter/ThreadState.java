package org.spl.vm.interpreter;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.exceptions.splexceptions.SPLStackOverflowError;
import org.spl.vm.types.SPLCommonType;

public class ThreadState {

  public static int maxCallStackSize = 100;

  public static ThreadLocal<ThreadState> tss;

  static {
    tss = new ThreadLocal<>();
  }

  private int callStackSize;
  private SPLCommonType execType;
  private SPLException execVal;
  private SPLTraceBackObject trace;

  public ThreadState() {
    callStackSize = 0;
  }

  public void increaseCallStackSize() throws SPLInternalException {
    callStackSize++;
    if (maxCallStackSize <= callStackSize) {
      SPLErrorUtils.splErrorFormat(new SPLStackOverflowError("Stack Overflow"));
    }
  }

  public void decreaseCallStackSize() {
    callStackSize--;
  }

  public static void increaseThreadCallStackSize() throws SPLInternalException {
    get().increaseCallStackSize();
  }

  public static void decreaseThreadCallStackSize() {
    get().decreaseCallStackSize();
  }

  public static ThreadState get() {
    ThreadState ts = tss.get();
    if (ts == null) {
      ts = new ThreadState();
      tss.set(ts);
    }
    return ts;
  }

  public static void set(ThreadState ts) {
    tss.set(ts);
  }

  public static void clearThreadState() {
    get().setExecType(null);
    get().setExecVal(null);
    get().setTrace(null);
  }

  public SPLCommonType getExecType() {
    return execType;
  }

  public void setExecType(SPLCommonType execType) {
    this.execType = execType;
  }

  public SPLException getExecVal() {
    return execVal;
  }

  public void setExecVal(SPLException execVal) {
    this.execVal = execVal;
  }

  public SPLTraceBackObject getTrace() {
    return trace;
  }

  public void setTrace(SPLTraceBackObject trace) {
    this.trace = trace;
  }
}
