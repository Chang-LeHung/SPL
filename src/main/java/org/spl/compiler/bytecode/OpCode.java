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
  INPLACE_LSHIFT,
  INPLACE_RSHIFT,
  INPLACE_U_RSHIFT,
  INPLACE_AND,
  INPLACE_OR,
  INPLACE_XOR,
  INPLACE_ADD,
  INPLACE_SUB,
  INPLACE_MUL,
  INPLACE_DIV,
  INPLACE_TRUE_DIV,
  INPLACE_MOD,
  INPLACE_POWER,
  BUILD_MAP,
  BUILD_LIST,
  BUILD_SET,
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
  LOAD_ATTR,
  STORE_ATTR,
  POP,
  DUP,
  JUMP_FALSE,
  JMP_TRUE_NO_POP,
  JUMP_UNCON_FORWARD,
  JUMP_BACK,
  JUMP_BACK_TRUE,
  JUMP_ABSOLUTE,
  TRUE_DIV,
  NEG,
  RETURN,
  RETURN_NONE,
  MAKE_FUNCTION;
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
