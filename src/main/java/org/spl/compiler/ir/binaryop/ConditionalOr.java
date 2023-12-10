package org.spl.compiler.ir.binaryop;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.stmt.controlflow.JumpContext;

import java.util.List;

public class ConditionalOr extends AbstractBinaryExp<Instruction> {

  private List<IRNode<Instruction>> children;

  public ConditionalOr(IRNode<Instruction> left, IRNode<Instruction> right) {
    super(left, right, Op.CONDITIONAL_OR);
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {

    JumpContext auxContext = new JumpContext(context);
    getRight().accept(auxContext);
    // size of instruction CONDITIONAL_OR is 2 bytes
    context.add(new Instruction(OpCode.JMP_TRUE_NO_POP, auxContext.getCodeSize() + 2), getLineNo(), getColumnNo(), getLen());
    getRight().accept(context);
    context.add(new Instruction(OpCode.CONDITIONAL_OR), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(getLeft());
    }
    return children;
  }
}
