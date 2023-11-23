package org.spl.vm.internal.objs;

import org.spl.vm.internal.typs.SPLFrameType;
import org.spl.vm.objects.SPLObject;

public class SPLFrameObject extends SPLObject {
  public SPLFrameObject() {
    super(SPLFrameType.getInstance());
  }
}
