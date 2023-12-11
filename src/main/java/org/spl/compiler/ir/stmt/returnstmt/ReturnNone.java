package org.spl.compiler.ir.stmt.returnstmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class ReturnNone extends AbstractIR<Instruction> {
  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    if (context.isTryBlockEnabled()) {
      ProgramBlock fb = context.getFinallyBlock();
      assert fb != null;
      fb.accept(context);
    }
    context.addInstruction(new Instruction(OpCode.RETURN_NONE), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
