package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FuncCallExp extends AbstractIR<Instruction> {
  private final List<IRNode<Instruction>> args;
  private final IRNode<Instruction> lhs;
  private List<IRNode<Instruction>> children;

  public FuncCallExp(IRNode<Instruction> lhs, List<IRNode<Instruction>> args) {
    this.args = args;
    this.lhs = lhs;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.CALL, args.size()), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public Op getOperator() {
    return Op.CALL;
  }

  @Override
  public String toString() {
    return lhs.toString() + "(" +
        args.stream().map(Object::toString).collect(Collectors.joining(", ")) +
        ")";
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize();
    context.decreaseStackSize(args.size());
    context.increaseStackSize(); // return val
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = new ArrayList<>(args);
      Collections.reverse(children);
      children.add(lhs);
    }
    return children;
  }
}
