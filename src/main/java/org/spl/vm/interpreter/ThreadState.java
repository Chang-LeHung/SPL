package org.spl.vm.interpreter;

import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.types.SPLCommonType;

public class ThreadState {

  public static ThreadLocal<ThreadState> tss;

  static {
    tss = new ThreadLocal<>();
  }

  private SPLCommonType execType;
  private SPLException execVal;
  private SPLTraceBackObject trace;

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
