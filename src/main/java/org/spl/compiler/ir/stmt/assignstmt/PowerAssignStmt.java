package org.spl.compiler.ir.stmt.assignstmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class PowerAssignStmt extends AbstractAssignStmt {
  public PowerAssignStmt(IRNode<Instruction> lhs, IRNode<Instruction> rhs) {
    super(lhs, rhs, Op.ASSIGN_POWER);
  }
}
