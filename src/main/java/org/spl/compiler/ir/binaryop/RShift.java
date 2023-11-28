package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class RShift extends AbstractBinaryExp<Instruction> {
  public RShift(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.RSHIFT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.add(new Instruction(OpCode.RSHIFT), getLineNo(), getColumnNo(), getLen());
  }
}
