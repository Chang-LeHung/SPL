package org.spl.compiler.ir.stmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class OrAssignStmt extends AbstractAssignStmt {
  public OrAssignStmt(AbstractIR<Instruction> lhs, AbstractIR<Instruction> rhs) {
    super(lhs, rhs, Op.ASSIGN_OR);
  }
}
