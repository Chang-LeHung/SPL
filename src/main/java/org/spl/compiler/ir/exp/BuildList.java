package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.ArrayList;
import java.util.List;

public class BuildList extends AbstractIR<Instruction> {
  private List<IRNode<Instruction>> children;

  public BuildList() {
    children = new ArrayList<>();
  }

  public void addIR(IRNode<Instruction> node) {
    children.add(node);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.BUILD_LIST, children.size()), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return children;
  }

  public void setChildren(List<IRNode<Instruction>> children) {
    this.children = children;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize(children.size());
    context.increaseStackSize();
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
