package org.spl.compiler.bytecode;

public enum OpCode {

  NOP,
  ADD,
  SUB,
  MUL,
  DIV,
  MOD,
  POWER,
  XOR,
  LSHIFT,
  RSHIFT,
  U_RSHIFT,
  LSHIFT_ASSIGN,
  RSHIFT_ASSIGN,
  U_RSHIFT_ASSIGN,
  AND_ASSIGN,
  OR_ASSIGN,
  XOR_ASSIGN,
  ADD_ASSIGN,
  SUB_ASSIGN,
  MUL_ASSIGN,
  DIV_ASSIGN,
  MOD_ASSIGN,
  POWER_ASSIGN,
  LT,
  GT,
  EQ,
  NE,
  LE,
  GE,
  AND,
  OR,
  INVERT,
  CONDITIONAL_AND,
  CONDITIONAL_OR,
  NOT,
  STORE_LOCAL,
  LOAD_LOCAL,
  STORE_GLOBAL,
  LOAD_GLOBAL,
  LOAD_NAME,
  LOAD_METHOD,
  CALL_METHOD,
  STORE,
  LOAD,
  CALL,
  LOAD_CONST,
  POP,
  JUMP_FALSE,
  JUMP_UNCON,
  TRUE_DIV,
  NEG,
  RETURN,
  RETURN_NONE;
  public final byte val;

  OpCode() {
    this.val = Counter.count++;
  }

  public static void main(String[] args) {
    System.out.println(CALL.val);
  }

  private static class Counter {
    static byte count = 0;
  }

  public byte getVal() {
    return val;
  }

  @Override
  public String toString() {
    return String.format("OpCode{val=%-3d, %-15s}", val, name());
  }
}
