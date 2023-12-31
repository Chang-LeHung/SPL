package org.spl.compiler.ir.unaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class NOP extends AbstractIR<Instruction> {
  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.NOP), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }
}
