package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class GreaterThan extends AbstractBinaryExp<Instruction> {
  public GreaterThan(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.GT);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {

    context.add(new Instruction(OpCode.GT), getLineNo(), getColumnNo(), getLen());
  }
}
