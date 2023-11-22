package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.Scope;

import java.util.List;

public class MethodCall extends AbstractIR<Instruction> {

  private final String objName;
  private final String methodName;
  private final List<AbstractIR<Instruction>> args;
  private final Scope scope;
  private List<AbstractIR<Instruction>> children;

  public MethodCall(String objName,
                    String methodName,
                    List<AbstractIR<Instruction>> args,
                    Scope scope) {
    this.objName = objName;
    this.methodName = methodName;
    this.args = args;
    this.scope = scope;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    args.forEach(x -> x.codeGen(context));
    byte ond = (byte) context.getConstantIndex(methodName);
    context.addInstruction(new Instruction(OpCode.LOAD_METHOD, ond));
    ond = (byte) context.getConstantIndex(objName);
    switch (scope) {
      case LOCAL -> {
        context.addInstruction(new Instruction(OpCode.LOAD_LOCAL, ond));
      }
      case GLOBAL -> {
        context.addInstruction(new Instruction(OpCode.LOAD_GLOBAL, ond));
      }
      case OTHERS -> {
        context.addInstruction(new Instruction(OpCode.LOAD_NAME, ond));
      }
    }
    context.addInstruction(new Instruction(OpCode.CALL_METHOD, (byte) args.size()));
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
    context.increaseStackSize();
    context.decreaseStackSize(args.size() + 2);
    context.increaseStackSize(); // return val
  }

  @Override
  public List<AbstractIR<Instruction>> getChildren() {
    if (children == null) {
      children = List.of();
    }
    return children;
  }
}
