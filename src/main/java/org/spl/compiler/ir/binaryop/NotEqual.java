package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

public class NotEqual extends AbstractBinaryExp<Instruction> {
  public NotEqual(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.NE);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.add(new Instruction(OpCode.NE), getLineNo(), getColumnNo(), getLen());
  }
}
