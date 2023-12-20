package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class IfStmt extends AbstractIR<Instruction> {

  private final IRNode<Instruction> condition;
  private final IRNode<Instruction> thenBlock;
  private final IRNode<Instruction> elseBlock;
  private List<IRNode<Instruction>> children;

  public IfStmt(IRNode<Instruction> condition, IRNode<Instruction> thenBlock, IRNode<Instruction> elseBlock) {
    this.condition = condition;
    this.thenBlock = thenBlock;
    this.elseBlock = elseBlock;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    JumpContext innerContex = new JumpContext(context);
    thenBlock.accept(innerContex);
//    thenBlock.doVisit(innerContex);
    int size = innerContex.getNBytes();
    context.addInstruction(new Instruction(OpCode.JUMP_FALSE, size), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
    thenBlock.accept(context);
    if (elseBlock != null) {
      innerContex = new JumpContext(context);
      elseBlock.accept(innerContex);
//      elseBlock.doVisit(innerContex);
      size = innerContex.getNBytes();
      context.addInstruction(new Instruction(OpCode.JUMP_UNCON_FORWARD, size), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
      elseBlock.accept(context);
    }
  }

  @Override
  public Op getOperator() {
    return Op.NOP;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null)
      children = List.of(condition);
    return children;
  }

  public IRNode<Instruction> getCondition() {
    return condition;
  }

  public IRNode<Instruction> getThenBlock() {
    return thenBlock;
  }

  public IRNode<Instruction> getElseBlock() {
    return elseBlock;
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
