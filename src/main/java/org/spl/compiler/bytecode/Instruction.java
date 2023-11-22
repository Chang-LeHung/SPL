package org.spl.compiler.bytecode;


import org.spl.compiler.tree.InsPickle;
import org.spl.compiler.tree.Visitor;

public class Instruction implements InsPickle {

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
    return String.format("%-11s, %d", code, opArg);
  }

  @Override
  public <T extends Visitor> void accept(T t) {
    t.visit(this);
  }
}
