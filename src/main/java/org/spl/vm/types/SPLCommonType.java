package org.spl.vm.types;

import org.spl.vm.objects.SPLObject;

import java.util.Deque;
import java.util.LinkedList;

public class SPLCommonType extends SPLObject {

  private final SPLCommonType base;
  private final String name;
  private final Class<? extends SPLObject> clazz;

  public SPLCommonType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type);
    base = SPLObjectType.getInstance();
    this.name = name;
    this.clazz = clazz;
  }

  public SPLCommonType getBase() {
    return base;
  }

  public String getName() {
    return name;
  }

  public Class<? extends SPLObject> getObjectClazz() {
    return clazz;
  }

  public static boolean isExecMatch(SPLObject o1, SPLCommonType o2) {
    if (o1 == null) return false;
    SPLCommonType type = o1.getType();
    while (type != null) {
      if (type == o2)
        return true;
      type = type.getBase();
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("<class %s @0x%s>", getType().getName(), Integer.toHexString(hashCode()));
  }
}
