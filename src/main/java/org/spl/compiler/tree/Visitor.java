package org.spl.compiler.tree;

import org.spl.compiler.ir.IRNode;

public interface Visitor<E> {

  default void visit(E instruction) {
    throw new RuntimeException("Not implemented");
  }

  default void visit(IRNode<E> node) {
    throw new RuntimeException("Not implemented");
  }
}
