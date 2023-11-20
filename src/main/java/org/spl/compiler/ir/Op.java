package org.spl.compiler.ir;

public enum Op {

  NOP(""),
  ADD("+"),
  SUB("-"),
  MUL("*"),
  DIV("/"),
  MOD("%"),
  LT("<"),
  GT(">"),
  LE("<="),
  GE(">="),
  EQ("=="),
  NE("!="),
  AND("&"),
  OR("|"),
  NOT("!"),
  XOR("^"),
  INVERT("~"),
  CONDITIONAL_AND("&&"),
  CONDITIONAL_OR("||"),
  POWER("**"),
  LSHIFT("<<"),
  RSHIFT(">>"),
  U_LSHIFT("<<<"),
  ASSIGN("=");

  private final String op;

  Op(String op) {
    this.op = op;
  }

  @Override
  public String toString() {
    return op;
  }
}
