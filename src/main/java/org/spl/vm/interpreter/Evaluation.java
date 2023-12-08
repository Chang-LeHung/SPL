package org.spl.vm.interpreter;

import org.spl.compiler.bytecode.OpCode;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.objects.SPLObject;

public interface Evaluation {

  OpCode[] opcode = new OpCode[256];

  static void init() {
    opcode[OpCode.NOP.val] = OpCode.NOP;
    opcode[OpCode.ADD.val] = OpCode.ADD;
    opcode[OpCode.SUB.val] = OpCode.SUB;
    opcode[OpCode.MUL.val] = OpCode.MUL;
    opcode[OpCode.DIV.val] = OpCode.DIV;
    opcode[OpCode.TRUE_DIV.val] = OpCode.TRUE_DIV;
    opcode[OpCode.MOD.val] = OpCode.MOD;
    opcode[OpCode.POWER.val] = OpCode.POWER;
    opcode[OpCode.NEG.val] = OpCode.NEG;
    opcode[OpCode.NOT.val] = OpCode.NOT;
    opcode[OpCode.INVERT.val] = OpCode.INVERT;
    opcode[OpCode.LT.val] = OpCode.LT;
    opcode[OpCode.GT.val] = OpCode.GT;
    opcode[OpCode.LE.val] = OpCode.LE;
    opcode[OpCode.GE.val] = OpCode.GE;
    opcode[OpCode.EQ.val] = OpCode.EQ;
    opcode[OpCode.NE.val] = OpCode.NE;
    opcode[OpCode.AND.val] = OpCode.AND;
    opcode[OpCode.OR.val] = OpCode.OR;
    opcode[OpCode.LSHIFT.val] = OpCode.LSHIFT;
    opcode[OpCode.RSHIFT.val] = OpCode.RSHIFT;
    opcode[OpCode.U_RSHIFT.val] = OpCode.U_RSHIFT;
    opcode[OpCode.INPLACE_ADD.val] = OpCode.INPLACE_ADD;
    opcode[OpCode.INPLACE_SUB.val] = OpCode.INPLACE_SUB;
    opcode[OpCode.INPLACE_MUL.val] = OpCode.INPLACE_MUL;
    opcode[OpCode.INPLACE_DIV.val] = OpCode.INPLACE_DIV;
    opcode[OpCode.INPLACE_MOD.val] = OpCode.INPLACE_MOD;
    opcode[OpCode.INPLACE_AND.val] = OpCode.INPLACE_AND;
    opcode[OpCode.INPLACE_OR.val] = OpCode.INPLACE_OR;
    opcode[OpCode.INPLACE_LSHIFT.val] = OpCode.INPLACE_LSHIFT;
    opcode[OpCode.INPLACE_RSHIFT.val] = OpCode.INPLACE_RSHIFT;
    opcode[OpCode.INPLACE_U_RSHIFT.val] = OpCode.INPLACE_U_RSHIFT;
    opcode[OpCode.INPLACE_POWER.val] = OpCode.INPLACE_POWER;
    opcode[OpCode.INPLACE_AND.val] = OpCode.INPLACE_AND;
    opcode[OpCode.INPLACE_XOR.val] = OpCode.INPLACE_XOR;
    opcode[OpCode.XOR.val] = OpCode.XOR;
    opcode[OpCode.INPLACE_LSHIFT.val] = OpCode.INPLACE_LSHIFT;
    opcode[OpCode.INPLACE_RSHIFT.val] = OpCode.INPLACE_RSHIFT;
    opcode[OpCode.INPLACE_U_RSHIFT.val] = OpCode.INPLACE_U_RSHIFT;
    opcode[OpCode.NOP.val] = OpCode.NOP;
    opcode[OpCode.CALL.val] = OpCode.CALL;
    opcode[OpCode.JUMP_FALSE.val] = OpCode.JUMP_FALSE;
    opcode[OpCode.JMP_TRUE_NO_POP.val] = OpCode.JMP_TRUE_NO_POP;
    opcode[OpCode.JUMP_UNCON_FORWARD.val] = OpCode.JUMP_UNCON_FORWARD;
    opcode[OpCode.STORE_LOCAL.val] = OpCode.STORE_LOCAL;
    opcode[OpCode.STORE_GLOBAL.val] = OpCode.STORE_GLOBAL;
    opcode[OpCode.LOAD_LOCAL.val] = OpCode.LOAD_LOCAL;
    opcode[OpCode.LOAD_GLOBAL.val] = OpCode.LOAD_GLOBAL;
    opcode[OpCode.LOAD_CONST.val] = OpCode.LOAD_CONST;
    opcode[OpCode.LOAD_METHOD.val] = OpCode.LOAD_METHOD;
    opcode[OpCode.LOAD_NAME.val] = OpCode.LOAD_NAME;
    opcode[OpCode.STORE.val] = OpCode.STORE;
    opcode[OpCode.POP.val] = OpCode.POP;
    opcode[OpCode.NEG.val] = OpCode.NEG;
    opcode[OpCode.CONDITIONAL_AND.val] = OpCode.CONDITIONAL_AND;
    opcode[OpCode.CONDITIONAL_OR.val] = OpCode.CONDITIONAL_OR;
    opcode[OpCode.RETURN.val] = OpCode.RETURN;
    opcode[OpCode.RETURN_NONE.val] = OpCode.RETURN_NONE;
    opcode[OpCode.JUMP_BACK.val] = OpCode.JUMP_BACK;
    opcode[OpCode.JUMP_ABSOLUTE.val] = OpCode.JUMP_ABSOLUTE;
    opcode[OpCode.JUMP_BACK_TRUE.val] = OpCode.JUMP_BACK_TRUE;
    opcode[OpCode.MAKE_FUNCTION.val] = OpCode.MAKE_FUNCTION;
  }

  SPLObject evalFrame() throws SPLInternalException;
}
