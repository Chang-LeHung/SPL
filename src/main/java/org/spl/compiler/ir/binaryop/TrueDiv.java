package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

public class TrueDiv extends AbstractBinaryExp<Instruction> {

  public TrueDiv(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.TRUE_DIV);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.TRUE_DIV), getLineNo(), getColumnNo(), getLen());
  }
}
