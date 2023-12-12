package org.spl.vm.objects;

import org.spl.vm.types.SPLStopIterationType;

public class SPLStopIteration extends SPLObject {
  private SPLStopIteration() {
    super(SPLStopIterationType.getInstance());
  }

  public static SPLStopIteration getInstance() {
    return SelfHolder.instance;
  }

  public static class SelfHolder {
    public static final SPLStopIteration instance = new SPLStopIteration();
  }
}
