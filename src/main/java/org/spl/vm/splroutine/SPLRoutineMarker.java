package org.spl.vm.splroutine;

import org.spl.vm.objects.SPLObject;

public class SPLRoutineMarker extends SPLObject {

  private final SPLRoutineMarkerState state;

  public enum SPLRoutineMarkerState {
    BLOCKED,
    RUNNING,
    READY,
    WAITING,
    TERMINATED
  }

  private SPLRoutineMarker(SPLRoutineMarkerState state) {
    super(SPLRoutineMarkerType.getInstance());
    this.state = state;
  }

  public static SPLRoutineMarker PSEUDO_BLOCKED = new SPLRoutineMarker(SPLRoutineMarkerState.BLOCKED);
  public static SPLRoutineMarker RUNNING = new SPLRoutineMarker(SPLRoutineMarkerState.RUNNING);
  public static SPLRoutineMarker WAITING = new SPLRoutineMarker(SPLRoutineMarkerState.WAITING);
  public static SPLRoutineMarker READY = new SPLRoutineMarker(SPLRoutineMarkerState.READY);
  public static SPLRoutineMarker TERMINATED = new SPLRoutineMarker(SPLRoutineMarkerState.TERMINATED);

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
      case READY -> {
        return SPLRoutineObject.SPLRoutineState.READY;
      }
      default -> {
        return SPLRoutineObject.SPLRoutineState.RUNNING;
      }
    }
  }
}
