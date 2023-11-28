package org.spl.compiler.ir.block;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.ArrayList;
import java.util.List;

public class Program extends AbstractIR<Instruction> {

  private final List<IRNode<Instruction>> blocks;

  public Program() {
    blocks = new ArrayList<>();
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {

  }

  public void addProgramBlock(IRNode<Instruction> node) {
    blocks.add(node);
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return blocks;
  }
}
