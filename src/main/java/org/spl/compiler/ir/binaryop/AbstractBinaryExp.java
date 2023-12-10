package org.spl.compiler.ir.binaryop;

import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public abstract class AbstractBinaryExp<E> extends AbstractIR<E> {

  protected final IRNode<E> L;
  protected final IRNode<E> R;
  protected final Op op;
  private List<IRNode<E>> children;

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

  @Override
  public void postVisiting(ASTContext<E> context) {
    context.decreaseStackSize(2);
    context.increaseStackSize(1);
  }

  @Override
  public List<IRNode<E>> getChildren() {
    if (children == null) {
      children = List.of(L, R);
    }
    return children;
  }
}
