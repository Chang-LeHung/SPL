package org.spl.compiler.ir.exp;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.lexer.Lexer;

import java.util.List;

public class ArrayStyleStore extends AbstractIR<Instruction> {

  private final IRNode<Instruction> obj;
  private final IRNode<Instruction> sub;
  private final IRNode<Instruction> value;

  private final Lexer.TOKEN_TYPE opCode;

  public ArrayStyleStore(IRNode<Instruction> obj, IRNode<Instruction> sub, IRNode<Instruction> value, Lexer.TOKEN_TYPE opCode) {
    this.obj = obj;
    this.sub = sub;
    this.value = value;
    this.opCode = opCode;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    obj.accept(context);
    sub.accept(context);
    switch (opCode) {
      case ASSIGN -> {
        value.accept(context);
      }
      case ASSIGN_ADD -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_ADD), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_SUB -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_SUB), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_MUL -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_MUL), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_DIV -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_DIV), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_TRUE_DIV -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_TRUE_DIV), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_POWER -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_POWER), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_MOD -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_MOD), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_OR -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_OR), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_AND -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_AND), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_XOR -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_XOR), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
      case ASSIGN_LSHIFT -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_LSHIFT), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize();
      }
      case ASSIGN_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_RSHIFT), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize();
      }
      case ASSIGN_U_RSHIFT -> {
        context.addInstruction(new Instruction(OpCode.DUP2), getLineNo(), getColumnNo(), getLen());
        context.increaseStackSize(2);
        context.addInstruction(new Instruction(OpCode.SUBSCRIBE), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize(2);
        value.accept(context);
        context.addInstruction(new Instruction(OpCode.INPLACE_U_RSHIFT), getLineNo(), getColumnNo(), getLen());
        context.decreaseStackSize();
      }
    }
    context.addInstruction(new Instruction(OpCode.SUBSCRIBE_STORE), getLineNo(), getColumnNo(), getLen());
    context.decreaseStackSize(3);
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }
}
