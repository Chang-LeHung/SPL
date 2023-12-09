package org.spl.vm.internal.utils;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.tree.InsVisitor;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.interpreter.Evaluation;

import java.util.ArrayList;
import java.util.List;

public class Dissembler {

  private final SPLCodeObject codeObject;
  private String content;

  private List<Instruction> instructions;
  private final byte[] code;
  private int pc;

  public Dissembler(SPLCodeObject codeObject) {
    this.codeObject = codeObject;
    code = codeObject.getCode();
    pc = 0;
  }

  public Dissembler(SPLFuncObject funcObject) {
    this.codeObject = funcObject.getCodeObject();
    code = codeObject.getCode();
    pc = 0;
  }

  private void doParse() {
    instructions = new ArrayList<>();
    Instruction ins;
    while (pc < code.length) {
      switch (Evaluation.opcode[code[pc++] & 0xff]) {
        case RETURN_NONE -> {
          ins = new Instruction(OpCode.RETURN_NONE, 0);
          pc++;
        }
        case NOP -> {
          ins = new Instruction(OpCode.NOP, 0);
          pc++;
        }
        case ADD -> {
          ins = new Instruction(OpCode.ADD, 0);
          pc++;
        }
        case SUB -> {
          ins = new Instruction(OpCode.SUB, 0);
          pc++;
        }
        case MUL -> {
          ins = new Instruction(OpCode.MUL, 0);
          pc++;
        }
        case DIV -> {
          ins = new Instruction(OpCode.DIV, 0);
          pc++;
        }
        case TRUE_DIV -> {
          ins = new Instruction(OpCode.TRUE_DIV, 0);
          pc++;
        }
        case NEG -> {
          ins = new Instruction(OpCode.NEG, 0);
          pc++;
        }
        case MOD -> {
          ins = new Instruction(OpCode.MOD, 0);
          pc++;
        }
        case BUILD_LIST -> {
          ins = new Instruction(OpCode.BUILD_LIST, getOparg());
        }
        case BUILD_MAP -> {
          ins = new Instruction(OpCode.BUILD_MAP, getOparg());
        }
        case BUILD_SET -> {
          ins = new Instruction(OpCode.BUILD_SET, getOparg());
        }
        case POWER -> {
          ins = new Instruction(OpCode.POWER, 0);
          pc++;
        }
        case XOR -> {
          ins = new Instruction(OpCode.XOR, 0);
          pc++;
        }
        case LSHIFT -> {
          ins = new Instruction(OpCode.LSHIFT, 0);
          pc++;
        }
        case RSHIFT -> {
          ins = new Instruction(OpCode.RSHIFT, 0);
          pc++;
        }
        case U_RSHIFT -> {
          ins = new Instruction(OpCode.U_RSHIFT, 0);
          pc++;
        }
        case INPLACE_LSHIFT -> {
          ins = new Instruction(OpCode.INPLACE_LSHIFT, 0);
          pc++;
        }
        case INPLACE_RSHIFT -> {
          ins = new Instruction(OpCode.INPLACE_RSHIFT, 0);
          pc++;
        }
        case INPLACE_U_RSHIFT -> {
          ins = new Instruction(OpCode.INPLACE_U_RSHIFT, 0);
          pc++;
        }
        case INPLACE_AND -> {
          ins = new Instruction(OpCode.INPLACE_AND, 0);
          pc++;
        }
        case INPLACE_OR -> {
          ins = new Instruction(OpCode.INPLACE_OR, 0);
          pc++;
        }
        case INPLACE_XOR -> {
          ins = new Instruction(OpCode.INPLACE_XOR, 0);
          pc++;
        }
        case INPLACE_ADD -> {
          ins = new Instruction(OpCode.INPLACE_ADD, 0);
          pc++;
        }
        case INPLACE_SUB -> {
          ins = new Instruction(OpCode.INPLACE_SUB, 0);
          pc++;
        }
        case INPLACE_MUL -> {
          ins = new Instruction(OpCode.INPLACE_MUL, 0);
          pc++;
        }
        case INPLACE_DIV -> {
          ins = new Instruction(OpCode.INPLACE_DIV, 0);
          pc++;
        }
        case INPLACE_MOD -> {
          ins = new Instruction(OpCode.INPLACE_MOD, 0);
          pc++;
        }
        case INPLACE_POWER -> {
          ins = new Instruction(OpCode.INPLACE_POWER, 0);
          pc++;
        }
        case LT -> {
          ins = new Instruction(OpCode.LT, 0);
          pc++;
        }
        case GT -> {
          ins = new Instruction(OpCode.GT, 0);
          pc++;
        }
        case LE -> {
          ins = new Instruction(OpCode.LE, 0);
          pc++;
        }
        case GE -> {
          ins = new Instruction(OpCode.GE, 0);
          pc++;
        }
        case EQ -> {
          ins = new Instruction(OpCode.EQ, 0);
          pc++;
        }
        case NE -> {
          ins = new Instruction(OpCode.NE, 0);
          pc++;
        }
        case AND -> {
          ins = new Instruction(OpCode.AND, 0);
          pc++;
        }
        case OR -> {
          ins = new Instruction(OpCode.OR, 0);
          pc++;
        }
        case NOT -> {
          ins = new Instruction(OpCode.NOT, 0);
          pc++;
        }
        case INVERT -> {
          ins = new Instruction(OpCode.INVERT, 0);
          pc++;
        }
        case CONDITIONAL_AND -> {
          ins = new Instruction(OpCode.CONDITIONAL_AND, 0);
          pc++;
        }
        case CONDITIONAL_OR -> {
          ins = new Instruction(OpCode.CONDITIONAL_OR, 0);
          pc++;
        }
        case STORE_LOCAL -> {
          ins = new Instruction(OpCode.STORE_LOCAL, getOparg());
        }
        case LOAD_LOCAL -> {
          ins = new Instruction(OpCode.LOAD_LOCAL, getOparg());
        }
        case LOAD_METHOD -> {
          ins = new Instruction(OpCode.LOAD_METHOD, getOparg());
        }
        case LOAD_NAME -> {
          ins = new Instruction(OpCode.LOAD_NAME, getOparg());
        }
        case LOAD -> {
          ins = new Instruction(OpCode.LOAD, getOparg());
        }
        case LOAD_CONST -> {
          ins = new Instruction(OpCode.LOAD_CONST, getOparg());
        }
        case MAKE_FUNCTION -> {
          ins = new Instruction(OpCode.MAKE_FUNCTION, getOparg());
        }
        case STORE_GLOBAL -> {
          ins = new Instruction(OpCode.STORE_GLOBAL, getOparg());
        }
        case DUP -> {
          ins = new Instruction(OpCode.DUP, getOparg());
        }
        case INPLACE_TRUE_DIV -> {
          ins = new Instruction(OpCode.INPLACE_TRUE_DIV, 0);
        }
        case LOAD_GLOBAL -> {
          ins = new Instruction(OpCode.LOAD_GLOBAL, getOparg());
        }
        case CALL -> {
          ins = new Instruction(OpCode.CALL, getOparg());
        }
        case LOAD_ATTR -> {
          ins = new Instruction(OpCode.LOAD_ATTR, getOparg());
        }
        case STORE_ATTR -> {
          ins = new Instruction(OpCode.STORE_ATTR, getOparg());
        }
        case POP -> {
          ins = new Instruction(OpCode.POP, 0);
          pc++;
        }
        case JUMP_FALSE -> {
          ins = new Instruction(OpCode.JUMP_FALSE, getOparg());
        }
        case JMP_TRUE_NO_POP -> {
          ins = new Instruction(OpCode.JMP_TRUE_NO_POP, getOparg());
        }
        case JUMP_BACK -> {
          ins = new Instruction(OpCode.JUMP_BACK, getOparg());
        }
        case JUMP_UNCON_FORWARD -> {
          ins = new Instruction(OpCode.JUMP_UNCON_FORWARD, getOparg());
        }
        case JUMP_BACK_TRUE -> {
          ins = new Instruction(OpCode.JUMP_BACK_TRUE, getOparg());
        }
        case JUMP_ABSOLUTE -> {
          int arg = 0;
          arg |= code[pc++];
          arg <<= 8;
          arg |= code[pc++];
          arg <<= 8;
          arg |= code[pc++];
          ins = new Instruction(OpCode.JUMP_ABSOLUTE, arg);
        }
        case RETURN -> {
          ins = new Instruction(OpCode.RETURN, 0);
          pc++;
        }
        default -> {
          pc++;
          ins = new Instruction(OpCode.POP, 0);
        }
      }
      instructions.add(ins);
    }
  }

  public void prettyPrint() {
    if (instructions == null) {
      doParse();
      InsVisitor insVisitor = new InsVisitor(codeObject.getVarnames(), codeObject.getConstants());
      instructions.forEach(ins -> ins.accept(insVisitor));
      content = insVisitor.toString();
    }
    System.out.println(content);
  }

  public String getContent() {
    return content;
  }

  private int getOparg() {
    if (code[pc] == -1) {
      pc++;
      int arg = code[pc++];
      arg <<= 8;
      arg |= (code[pc++] & 0xff);
      return arg;
    } else {
      return code[pc++];
    }
  }
}
