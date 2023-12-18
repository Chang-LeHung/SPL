package org.spl.vm.internal.utils;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.typs.SPLRangeType;
import org.spl.vm.objects.SPLCommonIterator;
import org.spl.vm.objects.SPLLongObject;
import org.spl.vm.objects.SPLObject;

import java.util.ArrayList;

public class SPLRangeObject extends SPLObject {
  private final int start;
  private final int end;
  private final int step;

  public SPLRangeObject(int start, int end, int step) {
    super(SPLRangeType.getInstance());
    this.start = start;
    this.end = end;
    this.step = step;
  }

  @Override
  public SPLCommonIterator __getIterator__() throws SPLInternalException {
    ArrayList<SPLObject> container = new ArrayList<>();
    for (int i = start; i < end; i += step) {
      container.add(SPLLongObject.create(i));
    }
    return new SPLCommonIterator(container);
  }
}
