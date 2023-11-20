package org.spl.compiler.bytecode;

public class Instruction {

  private final OpCode code;
  private final byte opArg;

  public Instruction(OpCode code, byte opArg) {
    this.code = code;
    this.opArg = opArg;
  }

  public Instruction(OpCode code) {
    this.code = code;
    this.opArg = 0;
  }

  public OpCode getCode() {
    return code;
  }

  public byte getOpArg() {
    return opArg;
  }

  @Override
  public String toString() {
    return "ByteCode{" +
        "code=" + code +
        ", opArg=" + opArg +
        '}';
  }
}
