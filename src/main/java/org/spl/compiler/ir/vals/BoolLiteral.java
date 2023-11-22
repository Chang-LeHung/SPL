package org.spl.compiler.ir.vals;

import org.spl.compiler.ir.Op;

public class BoolLiteral extends Literal {

  public BoolLiteral(byte oparg) {
    super(oparg);
  }


  @Override
  public boolean isBooleanLiteral() {
    return true;
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }
}
