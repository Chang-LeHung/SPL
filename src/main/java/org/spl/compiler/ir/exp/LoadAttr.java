package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class LoadAttr extends AbstractIR<Instruction> {

  private final int attrIndex;
  private final String name;
  private IRNode<Instruction> lhs;
  private List<IRNode<Instruction>> children;

  public LoadAttr(IRNode<Instruction> lhs, int attrIndex, String name) {
    this.lhs = lhs;
    this.attrIndex = attrIndex;
    this.name = name;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.LOAD_ATTR, attrIndex), getLineNo(), getColumnNo(), getLen());
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

  public IRNode<Instruction> getLhs() {
    return lhs;
  }

  public void setLhs(IRNode<Instruction> lhs) {
    this.lhs = lhs;
  }

  public int getAttrIndex() {
    return attrIndex;
  }

  public String getName() {
    return name;
  }

}
