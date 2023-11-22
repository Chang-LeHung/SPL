package org.spl.compiler.ir.unaryop;

import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import java.util.List;

public abstract class AbstractUnaryExp<E> extends AbstractIR<E> {

  protected IRNode<E> operand;
  protected Op op;
  protected List<IRNode<E>> children;

  public AbstractUnaryExp(IRNode<E> operand, Op op) {
    this.operand = operand;
    this.op = op;
  }

  @Override
  public Op getOperator() {
    return op;
  }

  @Override
  public List<IRNode<E>> getChildren() {
    if (children == null) {
      children = List.of(operand);
    }
    return children;
  }
}
