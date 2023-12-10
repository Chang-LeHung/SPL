package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

public class ContinueVisitor extends JumpContext {
  private final int position;

  public ContinueVisitor(ASTContext<Instruction> context, int position) {
    super(context);
    this.position = position;
  }

  @Override
  public void visit(IRNode<Instruction> node) throws SPLSyntaxError {
    // Only need to handle the first layer of nesting.
    if (node instanceof DoWhile || node instanceof ForStmt || node instanceof WhileStmt)
      return;
    if (node instanceof Continue cont) {
      cont.setAbsoluteAddr(position);
    }
    super.visit(node);
  }
}
