package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class Sub extends AbstractBinaryExp<Instruction> {
  public Sub(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.SUB);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {

    context.add(new Instruction(OpCode.SUB), getLineNo(), getColumnNo(), getLen());
  }
}
