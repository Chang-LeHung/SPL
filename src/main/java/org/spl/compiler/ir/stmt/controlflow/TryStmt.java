package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class TryStmt extends AbstractIR<Instruction> {

  private final IRNode<Instruction> tryBlock;

  private final List<IRNode<Instruction>> catchBlock;

  private final IRNode<Instruction> finallyBlock;
  private List<IRNode<Instruction>> children;

  public TryStmt(IRNode<Instruction> tryBlock, List<IRNode<Instruction>> catchBlock, IRNode<Instruction> finallyBlock) {
    this.tryBlock = tryBlock;
    this.catchBlock = catchBlock;
    this.finallyBlock = finallyBlock;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    int startPc = context.getCodeSize();
    tryBlock.accept(context);
    int endPc = context.getCodeSize();
    int currentSize = context.getCodeSize();
    boolean needJmp = false;
    int codeSize = 0;
    if (finallyBlock != null) {
      needJmp = true;
    }
    int targetPc = currentSize;
    if (catchBlock.size() != 0) {
      needJmp = true;
      JumpContext auxContext = new JumpContext(context);
      for (IRNode<Instruction> node : catchBlock) {
        node.accept(auxContext);
      }
      codeSize += auxContext.getCodeSize();
    }
    if (needJmp) {
      targetPc += 4;
      codeSize <<= 1; // if codeSize & 1 == 1 then jump forward else jump backward
      codeSize |= 1;
      context.addInstruction(new Instruction(OpCode.LONG_JUMP, codeSize), tryBlock.getLineNo(), tryBlock.getColumnNo(), tryBlock.getLen());
      codeSize >>= 1;
    }
    ASTContext.JumpTableEntry entry = new ASTContext.JumpTableEntry(startPc, endPc, targetPc);
    context.addJumpTableEntry(entry);
    codeSize += 4; // for LONG_JUMP
    for (IRNode<Instruction> node : catchBlock) {
      if (node instanceof ExceptBlock exec) {
        exec.setTargetPc(codeSize + currentSize);
      }
    }
    for (IRNode<Instruction> node : catchBlock) {
      node.accept(context)  ;
    }
    if (finallyBlock != null)
      finallyBlock.accept(context);
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of();
    }
    return children;
  }
}
