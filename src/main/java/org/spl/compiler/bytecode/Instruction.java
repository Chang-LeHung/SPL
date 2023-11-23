package org.spl.compiler.bytecode;


import org.spl.compiler.tree.InsPickle;
import org.spl.compiler.tree.Visitor;

public class Instruction implements InsPickle, ByteCode {

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
    return String.format("%-11s, %d\t%-3d, %-3d", code, opArg, code.getVal(), opArg);
  }

  @Override
  public void accept(Visitor<Instruction> t) {
    t.visit(this);
  }

  @Override
  public int getOpCode() {
    return code.getVal();
  }

  @Override
  public int getOparg() {
    return opArg;
  }
}
