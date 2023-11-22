package org.spl.compiler.ir.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.Op;

import java.util.ArrayList;
import java.util.List;

public class ProgramBlock extends AbstractIR<Instruction> {

  private final List<AbstractIR<Instruction>> statements;

  public ProgramBlock(List<AbstractIR<Instruction>> statements) {
    this.statements = statements;
  }

  public ProgramBlock() {
    statements = new ArrayList<>();
  }

  public void addIRNode(AbstractIR<Instruction> node) {
    statements.add(node);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }

  @Override
  public List<AbstractIR<Instruction>> getChildren() {
    return statements;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("{\n");
    statements.forEach(node -> builder.append(node.toString()).append("\n"));
    builder.append("}\n");
    return builder.toString();
  }
}
