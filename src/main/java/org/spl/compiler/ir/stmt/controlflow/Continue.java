package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class Continue extends AbstractIR<Instruction> {

  private int absoluteAddr;

  public Continue() {
    this.absoluteAddr = -1;
  }

  public int getAbsoluteAddr() {
    return absoluteAddr;
  }

  public void setAbsoluteAddr(int absoluteAddr) {
    this.absoluteAddr = absoluteAddr;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.JUMP_ABSOLUTE, absoluteAddr), this.getLineNo(), this.getColumnNo(), 1);
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }

  @Override
  public String toString() {
    return "continue";
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
