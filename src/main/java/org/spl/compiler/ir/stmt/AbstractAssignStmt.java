package org.spl.compiler.ir.stmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

import java.util.List;

public class AbstractAssignStmt extends AbstractIR<Instruction> {

  private final AbstractIR<Instruction> lhs;
  private final AbstractIR<Instruction> rhs;
  private final Op op;
  private List<AbstractIR<Instruction>> children;

  public AbstractAssignStmt(AbstractIR<Instruction> lhs,
                            AbstractIR<Instruction> rhs,
                            Op op) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.op = op;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {
    byte opArg = 0;
    switch (op) {
      case ASSIGN_ADD -> {
        context.addInstruction(new Instruction(OpCode.ADD_ASSIGN, opArg));
      }
      case ASSIGN_SUB -> {
        context.addInstruction(new Instruction(OpCode.SUB_ASSIGN, opArg));
      }
      case ASSIGN_MUL -> {
        context.addInstruction(new Instruction(OpCode.MUL_ASSIGN, opArg));
      }
      case ASSIGN_DIV -> {
        context.addInstruction(new Instruction(OpCode.DIV_ASSIGN, opArg));
      }
      case ASSIGN_MOD -> {
        context.addInstruction(new Instruction(OpCode.MOD_ASSIGN, opArg));
      }
      case ASSIGN_LSHIFT -> {
        context.addInstruction(new Instruction(OpCode.LSHIFT_ASSIGN, opArg));
      }
      case ASSIGN_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.RSHIFT_ASSIGN, opArg));
      }
      case ASSIGN_U_LSHIFT -> {
        context.addInstruction(new Instruction(OpCode.U_LSHIFT_ASSIGN, opArg));
      }
      case ASSIGN_XOR -> {
        context.addInstruction(new Instruction(OpCode.XOR_ASSIGN, opArg));
      }
      case ASSIGN_AND -> {
        context.addInstruction(new Instruction(OpCode.AND_ASSIGN, opArg));
      }
      case ASSIGN_OR -> {
        context.addInstruction(new Instruction(OpCode.OR_ASSIGN, opArg));
      }
      case ASSIGN_POWER -> {
        context.addInstruction(new Instruction(OpCode.POWER_ASSIGN, opArg));
      }
    }
  }

  @Override
  public List<AbstractIR<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(lhs, rhs);
    }
    return children;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize(2);
  }

  @Override
  public String toString() {
    return lhs.toString() + " " + op + " " + rhs.toString();
  }

  @Override
  public Op getOperator() {
    return op;
  }
}
