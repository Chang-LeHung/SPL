package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class WhileStmt extends AbstractIR<Instruction> {

  private IRNode<Instruction> condition;
  private IRNode<Instruction> block;

  public WhileStmt(IRNode<Instruction> condition, IRNode<Instruction> block) {
    this.condition = condition;
    this.block = block;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    condition.accept(context);
    JumpContext auxContext = new JumpContext(context);
    condition.accept(auxContext);
    int conditionSize = auxContext.getCodeSize();
    block.accept(auxContext);
    int codeSize = auxContext.getCodeSize();
    int blockSize = codeSize - conditionSize;
    if (isOK(2, 2, conditionSize, blockSize)) {
      context.addInstruction(new Instruction(OpCode.JUMP_FALSE, blockSize + 2), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
      block.accept(context);
      context.addInstruction(new Instruction(OpCode.JUMP_BACK, conditionSize + blockSize + 4),
          block.getLineNo(), block.getColumnNo(), block.getLen());
    } else if (isOK(4, 4, conditionSize, blockSize)) {
      context.addInstruction(new Instruction(OpCode.JUMP_FALSE, blockSize + 4), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
      block.accept(context);
      context.addInstruction(new Instruction(OpCode.JUMP_BACK, conditionSize + blockSize + 8),
          block.getLineNo(), block.getColumnNo(), block.getLen());
    } else if (isOK(2, 4, conditionSize, blockSize)) {
      context.addInstruction(new Instruction(OpCode.JUMP_FALSE, blockSize + 4), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
      block.accept(context);
      context.addInstruction(new Instruction(OpCode.JUMP_BACK, conditionSize + blockSize + 6),
          block.getLineNo(), block.getColumnNo(), block.getLen());
    } else {
      context.addInstruction(new Instruction(OpCode.JUMP_FALSE, blockSize + 2), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
      block.accept(context);
      context.addInstruction(new Instruction(OpCode.JUMP_BACK, conditionSize + blockSize + 6),
          block.getLineNo(), block.getColumnNo(), block.getLen());
    }
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }

  private static boolean isOK(int jmpFalse, int jmpBack, int conditionSize, int blockSize) {
    assert jmpBack == 2 || jmpBack == 4;
    assert jmpFalse == 2 || jmpFalse == 4;
    if (jmpBack == 2 && blockSize + conditionSize + jmpFalse < 255) {
      if (jmpFalse == 2 && blockSize + jmpBack < 255)
        return true;
      if (jmpFalse == 4 && blockSize + jmpBack >= 255)
        return true;
    }
    if (jmpBack == 4 && blockSize + conditionSize + jmpFalse >= 255) {
      if (jmpFalse == 2 && blockSize + jmpBack < 255)
        return true;
      return jmpFalse == 4 && blockSize + jmpBack >= 255;
    }
    return false;
  }
}
