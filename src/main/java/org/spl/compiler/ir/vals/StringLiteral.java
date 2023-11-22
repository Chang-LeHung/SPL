package org.spl.compiler.ir.vals;

import org.spl.compiler.ir.Op;

import static org.spl.compiler.ir.Op.NOP;

public class StringLiteral extends Literal {

  private final String val;

  public StringLiteral(String value, byte oparg) {
    super(oparg);
    val = value;
  }

  public String getVal() {
    return val;
  }


  @Override
  public boolean isStringLiteral() {
    return true;
  }

  @Override
  public Op getOperator() {
    return NOP;
  }
}
