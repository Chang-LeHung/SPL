package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class InplaceStoreAttr extends StoreAttr {

  private Op op;

  public InplaceStoreAttr(IRNode<Instruction> lhs, IRNode<Instruction> rhs, int attrIndex, String name, Op op) {
    super(lhs, rhs, attrIndex, name);
    this.op = op;
  }

  public Op getOp() {
    return op;
  }

  public void setOp(Op op) {
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
      case ASSIGN_TRUE_DIV -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_TRUE_DIV, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_MOD -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_MOD, 0), getLineNo(), getColumnNo(), getLen());
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
      case ASSIGN_AND -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_AND, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_OR -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_OR, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_XOR -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_XOR, 0), getLineNo(), getColumnNo(), getLen());
      }
      case ASSIGN_POWER -> {
        context.addInstruction(new Instruction(OpCode.INPLACE_POWER, 0), getLineNo(), getColumnNo(), getLen());
      }
    }
    super.codeGen(context);
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(lhs, rhs);
    }
    return children;
  }
}
