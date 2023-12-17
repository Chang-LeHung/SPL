package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class ArrayStyle extends AbstractIR<Instruction> {

  private final IRNode<Instruction> lhs;

  private final IRNode<Instruction> sub;
  private List<IRNode<Instruction>> children;

  public ArrayStyle(IRNode<Instruction> lhs, IRNode<Instruction> sub) {
    this.lhs = lhs;
    this.sub = sub;
  }

  public IRNode<Instruction> getLhs() {
    return lhs;
  }

  public IRNode<Instruction> getSub() {
    return sub;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(lhs, sub);
    }
    return children;
  }

  @Override
  public String toString() {
    return lhs.toString() + "[" + sub.toString() + "]";
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
