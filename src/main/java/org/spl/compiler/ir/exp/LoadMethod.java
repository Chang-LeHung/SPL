package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class LoadMethod extends AbstractIR<Instruction> {

  private final IRNode<Instruction> lhs;
  private final int attrIndex;
  private List<IRNode<Instruction>> children;
  private final String name;

  public LoadMethod(IRNode<Instruction> lhs, int attrIndex, String name) {
    this.lhs = lhs;
    this.attrIndex = attrIndex;
    this.name = name;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.LOAD_METHOD, attrIndex), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(lhs);
    }
    return children;
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public String toString() {
    return lhs.toString() + "." + name;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
  }
}

