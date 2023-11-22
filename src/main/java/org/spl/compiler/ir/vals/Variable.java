package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.Scope;

import java.util.List;

import static org.spl.compiler.ir.Op.NOP;

public class Variable extends AbstractIR<Instruction> {

  private final String name;
  private final Scope scope;
  private List<AbstractIR<Instruction>> children;

  public Variable(Scope scope, String name) {
    this.name = name;
    this.scope = scope;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    byte idx = (byte) context.getConstantIndex(name);
    switch (scope) {
      case LOCAL -> {
        context.add(new Instruction(OpCode.LOAD_LOCAL, idx));
      }
      case GLOBAL -> {
        context.add(new Instruction(OpCode.LOAD_GLOBAL, idx));
      }
      case OTHERS -> {
        context.add(new Instruction(OpCode.LOAD, idx));
      }
    }
  }

  public Scope scope() {
    return scope;
  }

  @Override
  public Op getOperator() {
    return NOP;
  }

  @Override
  public List<AbstractIR<Instruction>> getChildren() {
    if (children == null) {
      children = List.of();
    }
    return children;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean isVariable() {
    return true;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  public String getName() {
    return name;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
  }
}
