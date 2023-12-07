package org.spl.compiler.ir.stmt.returnstmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class Return extends AbstractIR<Instruction> {

  private final IRNode<Instruction> expr;
  private List<IRNode<Instruction>> children;

  public Return(IRNode<Instruction> expr) {
    this.expr = expr;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.add(new Instruction(OpCode.RETURN), this.getLineNo(), this.getColumnNo(), 0);
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(expr);
    }
    return children;
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
