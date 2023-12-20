package org.spl.compiler.ir.stmt.returnstmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class Return extends AbstractIR<Instruction> {

  private final IRNode<Instruction> expr;

  public Return(IRNode<Instruction> expr) {
    this.expr = expr;
  }

  public Return() {
    expr = null;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    if (context.isTryBlockEnabled()) {
      ProgramBlock fb = context.getFinallyBlock();
      assert fb != null;
      fb.accept(context);
    }
    if (expr != null) {
      expr.accept(context);
    }
    context.add(new Instruction(OpCode.RETURN), this.getLineNo(), this.getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }

  public IRNode<Instruction> getExpr() {
    return expr;
  }

  @Override
  public List<IRNode<Instruction>> getVisualizedChildren() {
    return List.of(expr);
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
