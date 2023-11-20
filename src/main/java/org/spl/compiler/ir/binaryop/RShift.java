package org.spl.compiler.ir;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;

import java.util.List;

public class RShift extends AbstractBinaryExp<Instruction>{
  public RShift(Node<Instruction> left, Node<Instruction> right) {
    super(left, right, Op.RSHIFT);
  }

  @Override
  public void codeGen(List<Instruction> container) {
    L.codeGen(container);
    R.codeGen(container);
    container.add(new Instruction(OpCode.RSHIFT));
  }
}
