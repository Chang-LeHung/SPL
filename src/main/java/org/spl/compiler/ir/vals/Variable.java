package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.Scope;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

import static org.spl.compiler.ir.Op.NOP;

public class Variable extends AbstractIR<Instruction> implements RValue<Instruction> {

  private final String name;
  private final IRNode<Instruction> rValue;
  private Scope scope;
  private List<IRNode<Instruction>> children;

  public Variable(Scope scope, String name) {
    this.name = name;
    this.scope = scope;
    rValue = null;
  }

  public Variable(Scope scope, String name, IRNode<Instruction> rValue) {
    this.name = name;
    this.scope = scope;
    this.rValue = rValue;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    int idx = context.getVarNameIndex(name);
    switch (scope) {
      case LOCAL -> {
        context.add(new Instruction(OpCode.LOAD_LOCAL, idx), getLineNo(), getColumnNo(), getLen());
      }
      case GLOBAL -> {
        context.add(new Instruction(OpCode.LOAD_GLOBAL, idx), getLineNo(), getColumnNo(), getLen());
      }
      case OTHERS -> {
        context.add(new Instruction(OpCode.LOAD_NAME, idx), getLineNo(), getColumnNo(), getLen());
      }
    }
  }

  public Scope scope() {
    return scope;
  }

  public Scope getScope() {
    return scope;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }

  @Override
  public Op getOperator() {
    return NOP;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      if (rValue == null)
        children = List.of();
      else
        children = List.of(rValue);
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

  public String getVariableName() {
    return name;
  }

  @Override
  public String getRValueName() {
    return name;
  }

  @Override
  public IRNode<Instruction> getRValueNode() {
    return rValue;
  }
}
