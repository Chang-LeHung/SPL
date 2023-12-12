package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.interfaces.SPLIterator;
import org.spl.vm.types.SPLCommonIteratorType;

import java.util.List;

public class SPLCommonIterator extends SPLObject implements SPLIterator {
  private final List<? extends SPLObject> container;
  private int off;

  public SPLCommonIterator(List<? extends SPLObject> container) {
    super(SPLCommonIteratorType.getInstance());
    this.container = container;
    off = 0;
  }

  @Override
  public SPLObject next() throws SPLInternalException {
    if (off >= container.size()) {
      return SPLStopIteration.getInstance();
    }
    return container.get(off++);
  }
}
