package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FuncCallExp extends AbstractIR<Instruction> {
  private final String funcName;
  private final List<IRNode<Instruction>> args;
  private List<IRNode<Instruction>> children;

  public FuncCallExp(String funcName, List<IRNode<Instruction>> args) {
    this.funcName = funcName;
    this.args = args;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.LOAD_NAME, context.getVarNameIndex(funcName)), getLineNo(), getColumnNo(), getLen());
    context.addInstruction(new Instruction(OpCode.CALL, args.size()), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public Op getOperator() {
    return Op.CALL;
  }

  @Override
  public String toString() {
    return funcName + "(" +
        args.stream().map(Object::toString).collect(Collectors.joining(", ")) +
        ")";
  }

  @Override
  public void preVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize(args.size() + 1);
    context.increaseStackSize(); // return val
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = new ArrayList<>(args);
      Collections.reverse(children);
    }
    return children;
  }
}
