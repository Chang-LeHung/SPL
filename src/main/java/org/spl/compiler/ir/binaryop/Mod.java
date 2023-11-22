package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;

public class Mod extends AbstractBinaryExp<Instruction> {
  public Mod(AbstractIR<Instruction> left, AbstractIR<Instruction> right) {
    super(left, right, Op.MOD);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) {

    context.add(new Instruction(OpCode.MOD));
  }
}
