package org.spl.compiler.ir.stmt.assignstmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.vals.Variable;

import java.util.List;

public class AssignStmt extends AbstractIR<Instruction> {

  private final Variable lhs;
  private final IRNode<Instruction> rhs;
  private List<IRNode<Instruction>> children;

  public AssignStmt(Variable lhs, IRNode<Instruction> rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    // lhs.codeGen(context); there is no need to emit this instruction
    // because a STORE instruction will emit after RHS
    int idx = context.getConstantIndex(lhs.getName());
    switch (lhs.scope()) {
      case LOCAL -> {
        context.add(new Instruction(OpCode.STORE_LOCAL, idx), getLineNo(), getColumnNo(), getLen());
      }
      case GLOBAL -> {
        context.add(new Instruction(OpCode.STORE_GLOBAL, idx), getLineNo(), getColumnNo(), getLen());
      }
      case OTHERS -> {
        // fallback to STORE
        context.add(new Instruction(OpCode.STORE, idx), getLineNo(), getColumnNo(), getLen());
      }
    }
  }

  @Override
  public Op getOperator() {
    return Op.ASSIGN;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(rhs);
    }
    return children;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize(1);
  }

  @Override
  public String toString() {
    return lhs.toString() + " = " + rhs.toString();
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
