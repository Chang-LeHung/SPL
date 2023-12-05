package org.spl.compiler.tree;

import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;

public interface Visitor<E> {

  default void visit(E instruction) {
    throw new RuntimeException("Not implemented");
  }

  default void visit(IRNode<E> node) throws SPLSyntaxError {
    throw new RuntimeException("Not implemented");
  }
}
