package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class Or extends AbstractBinaryExp<Instruction> {
  public Or(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.OR);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    L.codeGen(context);
    R.codeGen(context);
    context.add(new Instruction(OpCode.OR));
  }
}
