package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class ConditionalAnd extends AbstractBinaryExp<Instruction> {
  public ConditionalAnd(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.CONDITIONAL_AND);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {

    context.add(new Instruction(OpCode.CONDITIONAL_AND));
  }
}
