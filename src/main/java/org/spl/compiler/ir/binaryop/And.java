package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

public class And extends AbstractBinaryExp<Instruction> {
  public And(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.AND);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {

    context.add(new Instruction(OpCode.AND), getLineNo(), getColumnNo(), getLen());
  }
}
