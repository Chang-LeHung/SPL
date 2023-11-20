package org.spl.compiler.ir;

public abstract class AbstractBinaryExp<E> implements Node<E> {

  protected final Node<E> L;
  protected final Node<E> R;
  protected final Op op;

  public enum Op {
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    LT,
    GT,
    LE,
    GE,
    EQ,
    NE,
    AND,
    OR,
    POWER,
    LSHIFT,
    RSHIFT,
    U_LSHIFT
  }

  public AbstractBinaryExp(Node<E> left, Node<E> right, Op op) {
    L = left;
    R = right;
    this.op = op;
  }

  public Node<E> getLeft() {
    return L;
  }

  public Node<E> getRight() {
    return R;
  }

  public Op getOp() {
    return op;
  }
}
