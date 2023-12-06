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
    opcode[OpCode.ADD_ASSIGN.val] = OpCode.ADD_ASSIGN;
    opcode[OpCode.SUB_ASSIGN.val] = OpCode.SUB_ASSIGN;
    opcode[OpCode.MUL_ASSIGN.val] = OpCode.MUL_ASSIGN;
    opcode[OpCode.DIV_ASSIGN.val] = OpCode.DIV_ASSIGN;
    opcode[OpCode.MOD_ASSIGN.val] = OpCode.MOD_ASSIGN;
    opcode[OpCode.AND_ASSIGN.val] = OpCode.AND_ASSIGN;
    opcode[OpCode.OR_ASSIGN.val] = OpCode.OR_ASSIGN;
    opcode[OpCode.LSHIFT_ASSIGN.val] = OpCode.LSHIFT_ASSIGN;
    opcode[OpCode.RSHIFT_ASSIGN.val] = OpCode.RSHIFT_ASSIGN;
    opcode[OpCode.U_RSHIFT_ASSIGN.val] = OpCode.U_RSHIFT_ASSIGN;
    opcode[OpCode.POWER_ASSIGN.val] = OpCode.POWER_ASSIGN;
    opcode[OpCode.AND_ASSIGN.val] = OpCode.AND_ASSIGN;
    opcode[OpCode.XOR_ASSIGN.val] = OpCode.XOR_ASSIGN;
    opcode[OpCode.XOR.val] = OpCode.XOR;
    opcode[OpCode.LSHIFT_ASSIGN.val] = OpCode.LSHIFT_ASSIGN;
    opcode[OpCode.RSHIFT_ASSIGN.val] = OpCode.RSHIFT_ASSIGN;
    opcode[OpCode.U_RSHIFT_ASSIGN.val] = OpCode.U_RSHIFT_ASSIGN;
    opcode[OpCode.NOP.val] = OpCode.NOP;
    opcode[OpCode.CALL.val] = OpCode.CALL;
    opcode[OpCode.JUMP_FALSE.val] = OpCode.JUMP_FALSE;
    opcode[OpCode.JUMP_UNCON.val] = OpCode.JUMP_UNCON;
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
    opcode[OpCode.JUMP_BACK.val] = OpCode.JUMP_BACK;
    opcode[OpCode.JUMP_ABSOLUTE.val] = OpCode.JUMP_ABSOLUTE;
  }

  SPLObject evalFrame() throws SPLInternalException;
}
