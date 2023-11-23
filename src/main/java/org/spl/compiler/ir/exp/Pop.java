package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class Pop extends AbstractIR<Instruction> {
  @Override
  public void codeGen(ASTContext<Instruction> context) {
    context.addInstruction(new Instruction(OpCode.POP), getLineNo(), getColumnNo(), 1);
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }
}
