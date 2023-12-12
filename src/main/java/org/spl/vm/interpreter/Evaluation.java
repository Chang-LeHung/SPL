package org.spl.vm.interpreter;

import org.spl.compiler.bytecode.OpCode;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.objects.SPLObject;

import java.util.Arrays;

public interface Evaluation {

  OpCode[] opcode = new OpCode[256];

  static void init() {
    Arrays.stream(OpCode.values()).forEach(op -> opcode[op.val] = op);
  }

  SPLObject evalFrame() throws SPLInternalException;
}
