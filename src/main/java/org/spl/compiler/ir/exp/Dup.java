package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class Dup extends AbstractIR<Instruction> {
  private IRNode<Instruction> node;

  public Dup(IRNode<Instruction> node) {
    this.node = node;
  }

  public Dup() {

  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.DUP), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (node != null)
      return List.of(node);
    return List.of();
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public String toString() {
    if (node != null) {
      return node.toString();
    }
    return "Dup";
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
  }
}
