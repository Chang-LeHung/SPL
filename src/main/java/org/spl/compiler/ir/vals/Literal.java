package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class Literal extends AbstractIR<Instruction> {

  private final int oparg;

  public Literal(int oparg) {
    this.oparg = oparg;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.add(new Instruction(OpCode.LOAD_CONST, oparg), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }

  @Override
  public boolean isLiteral() {
    return true;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
  }
}
