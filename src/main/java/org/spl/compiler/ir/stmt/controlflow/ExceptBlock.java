package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class ExceptBlock extends AbstractIR<Instruction> {

  private final String exceptName;
  private final int exceptIdx;
  private final String storeName;
  private final int storeIdx;
  private int targetPc;

  private final IRNode<Instruction> block;

  public ExceptBlock(String exceptName, int exceptIdx, String storeName, int storeIdx, IRNode<Instruction> block) {
    this.exceptName = exceptName;
    this.exceptIdx = exceptIdx;
    this.storeName = storeName;
    this.storeIdx = storeIdx;
    this.block = block;
  }


  public String getExceptName() {
    return exceptName;
  }

  public int getExceptIdx() {
    return exceptIdx;
  }

  public String getStoreName() {
    return storeName;
  }

  public int getStoreIdx() {
    return storeIdx;
  }

  public IRNode<Instruction> getBlock() {
    return block;
  }

  public int getTargetPc() {
    return targetPc;
  }

  public void setTargetPc(int targetPc) {
    this.targetPc = targetPc;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    JumpContext auxContext = new JumpContext(context);
    block.accept(auxContext);
    int codeSize = auxContext.getCodeSize();
    codeSize += 4; // for ABSOLUTE_JUMP
    // for STORE_LOCAL
    codeSize += 2;
    if (storeIdx >= 255) {
      codeSize += 2;
    }
    int startPC = context.getCodeSize();
    context.addInstruction(new Instruction(OpCode.LOAD_NAME, exceptIdx), getLineNo(), getColumnNo(), getLen());
    context.addInstruction(new Instruction(OpCode.EXEC_MATCH, 0), getLineNo(), getColumnNo(), getLen());
    context.addInstruction(new Instruction(OpCode.JUMP_FALSE, codeSize), getLineNo(), getColumnNo(), getLen());
    context.addInstruction(new Instruction(OpCode.STORE_EXC_VAL, storeIdx), getLineNo(), getColumnNo(), getLen());
    block.accept(context);
    context.addInstruction(new Instruction(OpCode.JUMP_ABSOLUTE, targetPc), getLineNo(), getColumnNo(), getLen());
    int endPC = context.getCodeSize();
    context.addJumpTableEntry(new ASTContext.JumpTableEntry(startPC, endPC, targetPc));
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
    context.increaseStackSize();
    context.decreaseStackSize();
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }
}
