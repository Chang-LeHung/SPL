package org.spl.compiler.ir.vals;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import static org.spl.compiler.ir.Op.NOP;

public record Variable(Scope scope, int oparg, String name) implements IRNode<Instruction> {

  public enum Scope {
    LOCAL,
    GLOBAL,
    OTHERS
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    switch (scope) {
      case LOCAL -> {
        context.add(new Instruction(OpCode.LOAD_LOCAL, (byte) 0));
      }
      case GLOBAL -> {
        context.add(new Instruction(OpCode.LOAD_GLOBAL, (byte) 0));
      }
      case OTHERS -> {
        context.add(new Instruction(OpCode.LOAD, (byte) 0));
      }
    }
  }

  @Override
  public Op getOperator() {
    return NOP;
  }

  @Override
  public String toString() {
    return name();
  }

  @Override
  public boolean isVariable() {
    return true;
  }
}
