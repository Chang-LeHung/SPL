package org.spl.vm.splroutine;

import org.spl.vm.objects.SPLObject;

public class SPLRoutineMarker extends SPLObject {


  private boolean needReCall;

  private final SPLRoutineMarkerState state;

  public enum SPLRoutineMarkerState {
    BLOCKED,
    RUNNING,
    READY,
    WAITING,
    TIME_WAITING,
    TERMINATED
  }

  private SPLRoutineMarker(SPLRoutineMarkerState state, boolean needReCall) {
    super(SPLRoutineMarkerType.getInstance());
    this.state = state;
    this.needReCall = needReCall;
  }

  public static SPLRoutineMarker PSEUDO_BLOCKED = new SPLRoutineMarker(SPLRoutineMarkerState.BLOCKED, true);
  public static SPLRoutineMarker RUNNING = new SPLRoutineMarker(SPLRoutineMarkerState.RUNNING, true);
  public static SPLRoutineMarker WAITING = new SPLRoutineMarker(SPLRoutineMarkerState.WAITING, true);
  public static SPLRoutineMarker TIME_WAITING = new SPLRoutineMarker(SPLRoutineMarkerState.TIME_WAITING, false);
  public static SPLRoutineMarker READY = new SPLRoutineMarker(SPLRoutineMarkerState.READY, true);
  public static SPLRoutineMarker TERMINATED = new SPLRoutineMarker(SPLRoutineMarkerState.TERMINATED, false);

  public SPLRoutineObject.SPLRoutineState getState() {
    switch (state) {
      case BLOCKED -> {
        return SPLRoutineObject.SPLRoutineState.BLOCKED;
      }
      case WAITING -> {
        return SPLRoutineObject.SPLRoutineState.WAITING;
      }
      case TERMINATED -> {
        return SPLRoutineObject.SPLRoutineState.TERMINATED;
      }
      case TIME_WAITING -> {
        return SPLRoutineObject.SPLRoutineState.TIME_WAITING;
      }
      case READY -> {
        return SPLRoutineObject.SPLRoutineState.READY;
      }
      default -> {
        return SPLRoutineObject.SPLRoutineState.RUNNING;
      }
    }
  }

  public boolean isNeedReCall() {
    return needReCall;
  }

  public void setNeedReCall(boolean needReCall) {
    this.needReCall = needReCall;
  }
}
