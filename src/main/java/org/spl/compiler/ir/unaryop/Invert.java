package org.spl.compiler.ir.unaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class Invert extends AbstractUnaryExp<Instruction> {
  public Invert(IRNode<Instruction> operand) {
    super(operand, Op.INVERT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    operand.codeGen(context);
    context.addInstruction(new Instruction(OpCode.INVERT));
  }
}
