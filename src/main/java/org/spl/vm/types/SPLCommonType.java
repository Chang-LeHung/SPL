package org.spl.vm.types;

import org.spl.vm.objects.SPLObject;

import java.util.Deque;
import java.util.LinkedList;

public class SPLCommonType extends SPLObject {

  private final Deque<SPLCommonType> bases;
  private final String name;
  private final Class<? extends SPLObject> clazz;

  public SPLCommonType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type);
    bases = new LinkedList<>();
    this.bases.addFirst(SPLObjectType.getInstance());
    this.name = name;
    this.clazz = clazz;
  }

  public Deque<SPLCommonType> getBases() {
    return bases;
  }

  public void addBaseFront(SPLCommonType base) {
    bases.addFirst(base);
  }

  public void addBaseBack(SPLCommonType base) {
    bases.addLast(base);
  }

  public String getName() {
    return name;
  }

  public Class<? extends SPLObject> getObjectClazz() {
    return clazz;
  }
}
