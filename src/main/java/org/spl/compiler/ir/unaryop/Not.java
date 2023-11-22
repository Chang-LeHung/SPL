package org.spl.compiler.ir.unaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.Op;

public class Not extends AbstractUnaryExp<Instruction> {
  public Not(AbstractIR<Instruction> operand) {
    super(operand, Op.NOT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    operand.codeGen(context);
    context.add(new Instruction(OpCode.NOT));
  }
}
