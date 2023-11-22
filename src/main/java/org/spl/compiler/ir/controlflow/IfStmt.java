package org.spl.compiler.ir.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import java.util.List;

public class IfStmt extends AbstractIR<Instruction> {
  @Override
  public void codeGen(ASTContext<Instruction> context) {

  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return null;
  }
}
