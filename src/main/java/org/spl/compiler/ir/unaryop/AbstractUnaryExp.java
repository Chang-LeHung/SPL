package org.spl.compiler.ir.unaryop;

import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public abstract class AbstractUnaryExp<E> implements IRNode<E> {

  protected IRNode<E> operand;
  protected Op op;

  public AbstractUnaryExp(IRNode<E> operand, Op op) {
    this.operand = operand;
    this.op = op;
  }

  @Override
  public Op getOperator() {
    return op;
  }
}
