package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public record BoolLiteral(Object val, byte oparg) implements IRNode<Instruction> {

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    context.add(new Instruction(OpCode.LOAD_CONST, oparg));
  }

  @Override
  public boolean isLiteral() {
    return true;
  }

  @Override
  public boolean isBooleanLiteral() {
    return true;
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }
}
