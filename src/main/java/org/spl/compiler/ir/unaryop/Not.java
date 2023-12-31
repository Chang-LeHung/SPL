package org.spl.compiler.ir.unaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

public class Not extends AbstractUnaryExp<Instruction> {
  public Not(IRNode<Instruction> operand) {
    super(operand, Op.NOT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.add(new Instruction(OpCode.NOT), getLineNo(), getColumnNo(), getLen());
  }
}
