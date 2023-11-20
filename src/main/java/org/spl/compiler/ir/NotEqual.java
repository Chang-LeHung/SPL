package org.spl.compiler.ir;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;

import java.util.List;

public class NotEqual extends AbstractBinaryExp<Instruction> {
  public NotEqual(Node<Instruction> left, Node<Instruction> right) {
    super(left, right, Op.NE);
  }

  @Override
  public void codeGen(List<Instruction> container) {
    L.codeGen(container);
    R.codeGen(container);
    container.add(new Instruction(OpCode.NE));
  }
}
