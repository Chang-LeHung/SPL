package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class BoolLiteral implements IRNode<Instruction> {

  private final byte oparg;

  public BoolLiteral(byte oparg) {
    this.oparg = oparg;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    context.add(new Instruction(OpCode.LOAD_CONST, oparg));
  }

  public boolean getVal() {
    return oparg == 0; // 0 means true and 1 means false
  }

  @Override
  public boolean isLiteral() {
    return IRNode.super.isLiteral();
  }

  @Override
  public boolean isBooleanLiteral() {
    return IRNode.super.isBooleanLiteral();
  }

  @Override
  public Op getOperator() {
    return IRNode.super.getOperator();
  }
}
