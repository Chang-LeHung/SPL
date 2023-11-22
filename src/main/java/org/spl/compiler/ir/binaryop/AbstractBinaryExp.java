package org.spl.compiler.ir.binaryop;

import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import java.util.List;

public abstract class AbstractBinaryExp<E> extends AbstractIR<E> {

  protected final AbstractIR<E> L;
  protected final AbstractIR<E> R;
  protected final Op op;
  private List<AbstractIR<E>> children;

  public AbstractBinaryExp(AbstractIR<E> left, AbstractIR<E> right, Op op) {
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
  public List<AbstractIR<E>> getChildren() {
    if (children == null) {
      children = List.of(L, R);
    }
    return children;
  }
}
