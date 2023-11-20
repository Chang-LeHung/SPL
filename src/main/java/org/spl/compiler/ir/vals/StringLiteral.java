package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import static org.spl.compiler.ir.Op.NOP;

public class StringLiteral implements IRNode<Instruction> {

  private final byte oparg;
  private final String val;
  public StringLiteral(byte oparg, String value) {
    this.oparg = oparg;
    val = value;
  }

  public int getOparg() {
    return oparg;
  }

  public String getVal() {
    return val;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    context.add(new Instruction(OpCode.LOAD_CONST, oparg));
  }

  @Override
  public boolean isLiteral() {
    return true;
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
