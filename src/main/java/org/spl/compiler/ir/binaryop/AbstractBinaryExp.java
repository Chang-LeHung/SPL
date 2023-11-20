package org.spl.compiler.ir.binaryop;

import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public abstract class AbstractBinaryExp<E> implements IRNode<E> {

  protected final IRNode<E> L;
  protected final IRNode<E> R;
  protected final Op op;

  public AbstractBinaryExp(IRNode<E> left, IRNode<E> right, Op op) {
    L = left;
    R = right;
    this.op = op;
  }

  public IRNode<E> getLeft() {
    return L;
  }

  public IRNode<E> getRight() {
    return R;
  }

  @Override
  public Op getOperator() {
    return op;
  }

  @Override
  public String toString() {
    return L.toString() + " " + op.toString() + " " + R.toString();
  }
}
