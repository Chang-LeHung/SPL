package org.spl.compiler.ir.vals;

import org.spl.compiler.ir.Op;

public class IntLiteral extends Literal {

  private final int val;

  public IntLiteral(int val, int oparg) {
    super(oparg);
    this.val = val;
  }

  public int getVal() {
    return val;
  }

  @Override
  public boolean isIntLiteral() {
    return true;
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }
}
