package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodCall extends AbstractIR<Instruction> {

  private final String objName;
  private final String methodName;
  private final List<IRNode<Instruction>> args;
  private final Scope scope;
  private List<IRNode<Instruction>> children;

  public MethodCall(String objName,
                    String methodName,
                    List<IRNode<Instruction>> args,
                    Scope scope) {
    this.objName = objName;
    this.methodName = methodName;
    this.args = args;
    this.scope = scope;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    byte ond = (byte) context.getConstantIndex(methodName);
    context.addInstruction(new Instruction(OpCode.LOAD_METHOD, ond), getLineNo(), getColumnNo(), getLen());
    ond = (byte) context.getConstantIndex(objName);
    switch (scope) {
      case LOCAL -> {
        context.addInstruction(new Instruction(OpCode.LOAD_LOCAL, ond), getLineNo(), getColumnNo(), getLen());
      }
      case GLOBAL -> {
        context.addInstruction(new Instruction(OpCode.LOAD_GLOBAL, ond), getLineNo(), getColumnNo(), getLen());
      }
      case OTHERS -> {
        context.addInstruction(new Instruction(OpCode.LOAD_NAME, ond), getLineNo(), getColumnNo(), getLen());
      }
    }
    context.addInstruction(new Instruction(OpCode.CALL_METHOD, (byte) args.size()), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
    context.increaseStackSize();
    context.decreaseStackSize(args.size() + 2);
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
