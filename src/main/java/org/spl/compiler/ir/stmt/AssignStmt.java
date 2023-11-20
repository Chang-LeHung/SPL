package org.spl.compiler.ir.stmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.Scope;

public class AssignStmt implements IRNode<Instruction> {

  private final IRNode<Instruction> lhs;
  private final IRNode<Instruction> rhs;
  private final Scope scope;

  public AssignStmt(IRNode<Instruction> lhs, IRNode<Instruction> rhs,
                    Scope scope) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.scope = scope;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    lhs.codeGen(context);
    rhs.codeGen(context);
    switch (scope) {
      case LOCAL -> {
        context.add(new Instruction(OpCode.STORE_LOCAL, (byte) 0));
      }
      case GLOBAL -> {
        context.add(new Instruction(OpCode.STORE_GLOBAL, (byte) 0));
      }
      case OTHERS -> {
        context.add(new Instruction(OpCode.STORE, (byte) 0));
      }
    }
  }

  @Override
  public Op getOperator() {
    return Op.ASSIGN;
  }
}
