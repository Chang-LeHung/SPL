package org.spl.compiler.ir.unaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

public class Invert extends AbstractUnaryExp<Instruction> {
  public Invert(IRNode<Instruction> operand) {
    super(operand, Op.INVERT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.INVERT), getLineNo(), getColumnNo(), getLen());
  }
}
