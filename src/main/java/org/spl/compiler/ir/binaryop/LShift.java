package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class LShift extends AbstractBinaryExp<Instruction> {
  public LShift(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.LSHIFT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.add(new Instruction(OpCode.LSHIFT), getLineNo(), getColumnNo(), getLen());
  }
}
