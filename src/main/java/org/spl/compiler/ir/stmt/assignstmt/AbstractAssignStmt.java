package org.spl.compiler.ir.stmt.assignstmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.vals.Variable;

import java.util.List;

public class AbstractAssignStmt extends AbstractIR<Instruction> {

  private final IRNode<Instruction> lhs;
  private final IRNode<Instruction> rhs;
  private final Op op;
  private List<IRNode<Instruction>> children;

  public AbstractAssignStmt(IRNode<Instruction> lhs,
                            IRNode<Instruction> rhs,
                            Op op) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.op = op;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    byte opArg = (byte) context.getVarNameIndex(((Variable) lhs).getName());
    switch (op) {
      case ASSIGN_ADD -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_ADD, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_SUB -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_SUB, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_MUL -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_MUL, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_DIV -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_DIV, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_MOD -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_MOD, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_LSHIFT -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_LSHIFT, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_RSHIFT, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_U_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_U_RSHIFT, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_XOR -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_XOR, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_AND -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_AND, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_OR -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_OR, opArg), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_POWER -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_POWER, opArg), getLineNo(), getColumnNo(), getLen());
      }
    }
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(rhs);
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


  @Override
  public boolean isStatement() {
    return true;
  }
}
