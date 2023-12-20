package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class StoreAttr extends AbstractIR<Instruction> {
  protected final IRNode<Instruction> lhs;
  protected final IRNode<Instruction> rhs;
  protected final int attrIndex;
  protected final String name;
  protected List<IRNode<Instruction>> children;

  public StoreAttr(IRNode<Instruction> lhs, IRNode<Instruction> rhs, int attrIndex, String name) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.attrIndex = attrIndex;
    this.name = name;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.STORE_ATTR, attrIndex), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(lhs, rhs);
    }
    return children;
  }

  public IRNode<Instruction> getLhs() {
    return lhs;
  }

  public IRNode<Instruction> getRhs() {
    return rhs;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public String toString() {
    return lhs.toString() + "." + name + " = " + rhs.toString();
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize(2);
  }
}
