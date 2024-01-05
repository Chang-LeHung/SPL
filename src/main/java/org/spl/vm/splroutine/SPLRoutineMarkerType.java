package org.spl.vm.splroutine;

import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLRoutineMarkerType extends SPLCommonType {
  private SPLRoutineMarkerType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type, name, clazz);
  }

  public static class SelfHolder {
    public static SPLRoutineMarkerType self = new SPLRoutineMarkerType(null, "RoutineMarker", SPLRoutineMarker.class);
  }

  public static SPLRoutineMarkerType getInstance() {
    return SelfHolder.self;
  }
}
