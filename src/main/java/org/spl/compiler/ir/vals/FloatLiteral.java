package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public record FloatLiteral(float val, byte oparg) implements IRNode<Instruction> {

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    context.add(new Instruction(OpCode.LOAD_CONST, oparg));
  }

  @Override
  public boolean isLiteral() {
    return IRNode.super.isLiteral();
  }

  @Override
  public boolean isFloatLiteral() {
    return true;
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }
}
