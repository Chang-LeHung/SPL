package org.spl.compiler.ir.vals;

import org.spl.compiler.ir.Op;

public class FloatLiteral extends Literal {

  private final float val;

  public FloatLiteral(float val, byte oparg) {
    super(oparg);
    this.val = val;
  }

  @Override
  public boolean isFloatLiteral() {
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
