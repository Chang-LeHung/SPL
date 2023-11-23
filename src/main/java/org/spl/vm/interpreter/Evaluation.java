package org.spl.vm.interpreter;

import org.spl.vm.objects.SPLObject;

public interface Evaluation {

  SPLObject evalFrame();
}
