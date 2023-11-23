package org.spl.vm.types;

import org.spl.vm.objects.SPLObject;

import java.util.Deque;
import java.util.LinkedList;

public class SPLCommonType extends SPLObject {

  private final Deque<SPLCommonType> bases;
  private final String name;

  public SPLCommonType(SPLCommonType type, String name) {
    super(type);
    bases = new LinkedList<>();
    this.bases.addFirst(SPLObjectType.getInstance());
    this.name = name;
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
}
