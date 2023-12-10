package org.spl.compiler.ir.vals;

import org.spl.compiler.ir.IRNode;

public interface RValue<E> {

  String getRValueName();

  IRNode<E> getRValueNode();
}
