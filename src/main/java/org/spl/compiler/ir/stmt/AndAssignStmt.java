package org.spl.compiler.ir.stmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class AndAssignStmt extends AbstractAssignStmt {
  public AndAssignStmt(IRNode<Instruction> lhs, IRNode<Instruction> rhs) {
    super(lhs, rhs, Op.ASSIGN_AND);
  }
}
