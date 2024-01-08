package org.spl.vm.splroutine;

import org.spl.vm.annotations.SPLExportField;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.exceptions.splexceptions.SPLStackOverflowError;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.interpreter.Evaluation;
import org.spl.vm.interpreter.SPL;
import org.spl.vm.interpreter.SPLInternalWorld;
import org.spl.vm.interpreter.SPLTraceBackObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;
import org.spl.vm.types.SPLCommonType;

import java.util.concurrent.atomic.AtomicInteger;

public class SPLRoutineObject extends SPLObject implements SPLRoutineInterface {

  public enum SPLRoutineState {
    INIT,
    RUNNING,
    BLOCKED,
    READY,
    ERROR_OCCURRED,
    WAITING,
    TIME_WAITING,
    LOCK_WAITING,
    TERMINATED
  }

  public static AtomicInteger nonDaemonRoutineCount = new AtomicInteger(0);

  @SPLExportField
  private final SPLStringObject name;
  private final Evaluation eval;
  private volatile int priority;
  private volatile SPLRoutineState state;
  private volatile int callStackSize;
  private volatile SPLCommonType execType;
  private volatile SPLException execVal;
  private volatile SPLTraceBackObject trace;
  private volatile SPLFrameObject currentFrame;
  private final boolean isDaemon;

  public SPLRoutineObject(Evaluation eval, String name, boolean isDaemon) {
    super(SPLRoutineType.getInstance());
    this.eval = eval;
    priority = 0;
    state = SPLRoutineState.INIT;
    callStackSize = 0;
    this.name = new SPLStringObject(String.format("SPLRoutine<Name=%s>", name));
    if (!isDaemon)
      nonDaemonRoutineCount.incrementAndGet();
    this.isDaemon = isDaemon;
  }

  public SPLRoutineObject(Evaluation eval, String name) {
    this(eval, name, false);
  }

  public SPLRoutineObject(Evaluation eval, int priority, String name, boolean isDaemon) {
    super(SPLRoutineType.getInstance());
    this.eval = eval;
    this.priority = priority;
    checkAndShortenPriority();
    state = SPLRoutineState.INIT;
    callStackSize = 0;
    this.name = new SPLStringObject(String.format("SPLRoutine<Name=%s>", name));
    if (!isDaemon)
      nonDaemonRoutineCount.incrementAndGet();
    this.isDaemon = isDaemon;
  }

  public SPLRoutineObject(Evaluation eval, int priority, String name) {
    this(eval, priority, name, false);
  }

  @Override
  public void init() {

  }

  @Override
  public SPLRoutineState resume() {
    try {
      SPLObject res = eval.resume();
      if (res instanceof SPLRoutineMarker marker) {
        return marker.getState();
      }
      return state = SPLRoutineState.TERMINATED;
    } catch (SPLInternalException ignore) {
      SPL.printStackTrace();
    }
    return SPLRoutineState.ERROR_OCCURRED;
  }

  @Override
  public void destroy() {
    if (!isDaemon)
      nonDaemonRoutineCount.decrementAndGet();
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
    checkAndShortenPriority();
  }

  private void checkAndShortenPriority() {
    if (priority < -20)
      priority = -20;
    if (priority >= 20)
      priority = 19;
  }

  public Evaluation getEval() {
    return eval;
  }

  public int getCallStackSize() {
    return callStackSize;
  }

  public SPLCommonType getExecType() {
    return execType;
  }

  public SPLException getExecVal() {
    return execVal;
  }

  public SPLTraceBackObject getTrace() {
    return trace;
  }

  public SPLFrameObject getCurrentFrame() {
    return currentFrame;
  }

  @Override
  public SPLRoutineState getState() {
    return state;
  }

  public void setState(SPLRoutineState state) {
    this.state = state;
  }


  public void increaseCallStackSize() throws SPLInternalException {
    if (callStackSize < SPLInternalWorld.splWorld.getMaxCallStackSize()) {
      callStackSize++;
    } else {
      SPLErrorUtils.splErrorFormat(new SPLStackOverflowError("Call Stack Overflow"));
    }
  }

  public void decreaseCallStackSize() throws SPLInternalException {
    if (callStackSize > 0) {
      callStackSize--;
    } else {
      SPLErrorUtils.splErrorFormat(new SPLException("Call Stack Underflow"));
    }
  }

  public void setExecType(SPLCommonType execType) {
    this.execType = execType;
  }

  public void setExecVal(SPLException execVal) {
    this.execVal = execVal;
  }

  public void setTrace(SPLTraceBackObject trace) {
    this.trace = trace;
  }

  public void setCurrentFrame(SPLFrameObject currentFrame) {
    this.currentFrame = currentFrame;
  }

  public SPLStringObject getName() {
    return name;
  }

  public boolean isMainRoutine() {
    assert SPLInternalWorld.splWorld != null;
    return SPLInternalWorld.splWorld.getMainRoutine() == this;
  }
}
