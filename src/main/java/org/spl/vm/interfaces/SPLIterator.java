package org.spl.vm.interfaces;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.objects.SPLObject;

public interface SPLIterator {

  SPLObject next() throws SPLInternalException;
}
