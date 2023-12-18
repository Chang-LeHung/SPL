package org.spl.vm.internal.typs;

import org.spl.vm.internal.objs.SPLClassDefinition;
import org.spl.vm.types.SPLCommonType;

public class SPLClassDefinitionType extends SPLCommonType {
  private SPLClassDefinitionType() {
    super(null, "ClassDefinition", SPLClassDefinition.class);
  }

  private static class SelfHolder {
    public static final SPLClassDefinitionType INSTANCE = new SPLClassDefinitionType();
  }

  public static SPLClassDefinitionType getInstance() {
    return SelfHolder.INSTANCE;
  }
}
