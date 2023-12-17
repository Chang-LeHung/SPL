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
    switch (op) {
      case ASSIGN_ADD -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_ADD, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_SUB -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_SUB, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_MUL -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_MUL, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_DIV -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_DIV, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_MOD -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_MOD, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_TRUE_DIV -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_TRUE_DIV, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_LSHIFT -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_LSHIFT, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_RSHIFT, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_U_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_U_RSHIFT, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_XOR -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_XOR, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_AND -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_AND, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_OR -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_OR, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_POWER -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_POWER, 0), getLineNo(), getColumnNo(), getLen());
      }
    }
    var L = (Variable) lhs;
    int idx = L.getIdx();
    switch (L.scope()) {
      case LOCAL -> {
        context.add(new Instruction(OpCode.STORE_LOCAL, idx), getLineNo(), getColumnNo(), getLen());
      }
      case GLOBAL -> {
        context.add(new Instruction(OpCode.STORE_GLOBAL, idx), getLineNo(), getColumnNo(), getLen());
      }
      case CLOSURE -> {
        context.add(new Instruction(OpCode.STORE_CLOSURE, idx), getLineNo(), getColumnNo(), getLen());
      }
      case OTHERS -> {
        // fallback to STORE
        context.add(new Instruction(OpCode.STORE, idx), getLineNo(), getColumnNo(), getLen());
      }
    }
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(lhs, rhs);
    }
    return children;
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.decreaseStackSize(2);
    context.increaseStackSize();
    context.decreaseStackSize();
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
