package org.spl.compiler.ir.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import java.util.ArrayList;
import java.util.List;

public class ProgramBlock implements IRNode<Instruction> {

  private final List<IRNode<Instruction>> statements;

  public ProgramBlock(List<IRNode<Instruction>> statements) {
    this.statements = statements;
  }
  public ProgramBlock() {
    statements = new ArrayList<>();
  }

  public void addIRNode(IRNode<Instruction> node) {
    statements.add(node);
  }
  @Override
  public void codeGen(ASTContext<Instruction> context) {
    statements.forEach(node -> node.codeGen(context));
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }
}
