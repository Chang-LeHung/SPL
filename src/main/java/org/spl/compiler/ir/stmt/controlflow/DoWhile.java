package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class DoWhile extends AbstractIR<Instruction> {

  private final IRNode<Instruction> block;
  private final IRNode<Instruction> condition;

  public DoWhile(IRNode<Instruction> condition, IRNode<Instruction> block) {
    this.condition = condition;
    this.block = block;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    int size = context.getCodeSize();
    block.accept(context);
    condition.accept(context);
    int diff = context.getCodeSize() - size + 2;
    if (diff >= 255) {
      diff += 2;
    }
    context.addInstruction(new Instruction(OpCode.JUMP_BACK_TRUE, diff), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }
}
