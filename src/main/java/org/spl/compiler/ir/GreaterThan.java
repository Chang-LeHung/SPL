package org.spl.compiler.ir;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;

import java.util.List;

public class GreaterThan extends AbstractBinaryExp<Instruction> {
  public GreaterThan(Node<Instruction> left, Node<Instruction> right) {
    super(left, right, Op.GT);
  }

  @Override
  public void codeGen(List<Instruction> container) {
    L.codeGen(container);
    R.codeGen(container);
    container.add(new Instruction(OpCode.GT));
  }
}
